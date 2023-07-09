package ru.yandex.practicum.filmorate.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("")
@RequiredArgsConstructor
@Qualifier("userDbStorage")
public class UserDbService implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userDbStorage;

    @Override
    public Optional<User> addFriend(Integer id, Integer friendId) {
        String sql = "insert into friends(user_id, friend_id) values(?, ?)";
        jdbcTemplate.update(sql, id, friendId);
        return userDbStorage.getUserById(id);
    }

    @Override
    public Optional<User> deleteFriend(Integer id, Integer friendId) {
        String sql = "delete from friends where user_id = ? AND friend_id = ?";
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
        String sql = "select * from users where id in(" + queryCommonFriendsId + ")";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id, otherId);
    }

    @Override
    public List<User> showFriends(Integer id) {
        String queryUserFriendsId = "select friend_id from friends where user_id = ?";
        String sql = "select * from users where id in(" + queryUserFriendsId + ")";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    private List<User> makeUser(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        do {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setName(rs.getString("name"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            do {
                if (rs.getInt("friend_id") != 0) {
                    user.friends.add(rs.getInt("friend_id"));
                }
            } while (rs.next() && rs.getInt("id") == user.getId());
            users.add(user);
        } while (!rs.isAfterLast());
        return users;
    }
}