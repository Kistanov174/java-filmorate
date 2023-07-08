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

    @Override
    public Optional<User> addFriend(Integer id, Integer friendId) {
        String sql = "insert into friends(user_id, friend_id) values(?, ?)";
        friendId = checkUser(friendId);
        jdbcTemplate.update(sql, id, friendId);
        return userDbStorage.getUserById(id);
    }

    @Override
    public Optional<User> deleteFriend(Integer id, Integer friendId) {
        String sql = "delete from friends where user_id = ? AND friend_id = ?";
        friendId = checkUser(friendId);
        jdbcTemplate.update(sql, id, friendId);
        return userDbStorage.getUserById(id);
    }

    @Override
    public List<User> showCommonFriends(Integer id, Integer otherId) {
        String queryUserFriendsId = "select friend_id from friends where user_id = ?";
        String queryFriendFriendsId = "select friend_id from friends where user_id = ?";
        String queryCommonFriendsId = "select uf.friend_id from (" + queryUserFriendsId + ") as uf inner join ("
                + queryFriendFriendsId
                + ") as ff on uf.friend_id = ff.friend_id";
        String sql = "select * " +
               "from users as u left join friends as f on u.id = f.user_id where u.id in(" + queryCommonFriendsId + ")";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> userMaker.makeUser(rs), id, otherId).stream().findFirst().orElseGet(ArrayList::new);
    }

    @Override
    public List<User> showFriends(Integer id) {
        String queryUserFriendsId = "select friend_id from friends where user_id = ?";
        String sql = "select * " +
                "from users as u left join friends as f on u.id = f.user_id where u.id in(" + queryUserFriendsId + ")";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> userMaker.makeUser(rs), id).stream().findFirst().orElseGet(ArrayList::new);
    }

    public Integer checkUser(final Integer friendId) {
        Optional<User> friend = userDbStorage.getUserById(friendId);
        return friend.map(User::getId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + friendId + " doesn't exist " +
                        UserDbService.class.getSimpleName()));
    }
}