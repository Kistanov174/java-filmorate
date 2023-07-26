package ru.yandex.practicum.filmorate.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("userDbService")
@RequiredArgsConstructor
@Qualifier("userDbStorage")
public class UserDbService implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userDbStorage;
    private final UserMaker userMaker;
    private static final String INSERT_INTO_FRIENDS = "insert into friends(user_id, friend_id) values(?, ?)";
    private static final String DELETE_FROM_FRIENDS = "delete from friends where user_id = ? AND friend_id = ?";
    private static final String SELECT_COMMON_FRIENDS = "select * " +
            "from users as u left join friends as f on u.id = f.user_id where u.id in" +
            "(select uf.friend_id from (select friend_id from friends where user_id = ?) as uf " +
            "inner join (select friend_id from friends where user_id = ?) as ff on uf.friend_id = ff.friend_id)";
    private static final String SELECT_FRIENDS = "select * " +
            "from users as u left join friends as f on u.id = f.user_id where u.id in" +
            "(select friend_id from friends where user_id = ?)";

    @Override
    public void addFriend(Integer id, Integer friendId) {
        friendId = checkUser(friendId);
        jdbcTemplate.update(INSERT_INTO_FRIENDS, id, friendId);
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        friendId = checkUser(friendId);
        jdbcTemplate.update(DELETE_FROM_FRIENDS, id, friendId);
    }

    @Override
    public List<User> showCommonFriends(Integer id, Integer otherId) {
        return jdbcTemplate.query(SELECT_COMMON_FRIENDS,
                (rs, rowNum) -> userMaker.makeUser(rs), id, otherId).stream().findFirst().orElseGet(ArrayList::new);
    }

    @Override
    public List<User> showFriends(Integer id) {
        return jdbcTemplate.query(SELECT_FRIENDS,
                (rs, rowNum) -> userMaker.makeUser(rs), id).stream().findFirst().orElseGet(ArrayList::new);
    }

    public Integer checkUser(final Integer friendId) {
        Optional<User> friend = userDbStorage.getUserById(friendId);
        return friend.map(User::getId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + friendId + " doesn't exist " +
                        UserDbService.class.getSimpleName()));
    }
}