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
import java.time.LocalDate;
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
        String sql = "delete from friends(user_id, friend_id) values(?, ?)";
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
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id, otherId));
    }

    @Override
    public List<User> showFriends(Integer id) {
        String queryUserFriendsId = "select friend_id from friends where user_id = ?";
        String sql = "select * from users where id in(" + queryUserFriendsId + ")";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String email = rs.getString("email");
        String name = rs.getString("name");
        String login = rs.getString("login");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }
}