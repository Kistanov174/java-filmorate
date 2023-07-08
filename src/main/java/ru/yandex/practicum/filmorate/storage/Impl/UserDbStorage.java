package ru.yandex.practicum.filmorate.storage.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.Impl.UserMaker;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import javax.validation.Valid;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMaker userMaker;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserMaker userMaker) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMaker = userMaker;
    }

    @Override
    public Optional<List<User>> getAllUsers() {
        String sql = "select * from users as u left join friends as f on u.id = f.user_id order by u.id";
        return Optional.of(jdbcTemplate.queryForStream(sql, (rs, rowNum) -> userMaker.makeUser(rs)).findFirst()
                .orElseGet(ArrayList::new));
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        String sql = "select * from users as u left join friends as f on u.id = f.user_id where u.id = ?";
        return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(sql,
                                (rs, rowNum) -> userMaker.makeUser(rs), id))
                .stream()
                        .findFirst())
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " doesn't exist " +
                        UserController.class.getSimpleName()));
    }

    @Override
    public Optional<User> createUser(@Valid User user) {
        userMaker.validateUser(user);
        String sql = "insert into users(email, login, name, birthday) values(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        return getUserById(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public Optional<User> updateUser(@Valid User updatedUser) {
        Integer id = updatedUser.getId();
        getUserById(id).orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " doesn't exist " +
                UserController.class.getSimpleName()));
        userMaker.validateUser(updatedUser);
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";
        jdbcTemplate.update(sql,
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getName(),
                updatedUser.getBirthday(),
                updatedUser.getId());
        return getUserById(Objects.requireNonNull(id));
    }
}