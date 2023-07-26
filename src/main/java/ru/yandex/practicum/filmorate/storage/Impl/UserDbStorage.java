package ru.yandex.practicum.filmorate.storage.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.Objects;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMaker userMaker;
    private static final int EXPECTED_SIZE = 1;
    private static final String SELECT_ALL_USERS = "select * " +
            "from users as u left join friends as f on u.id = f.user_id order by u.id";
    private static final String SELECT_USER_BY_ID = "select * " +
            "from users as u left join friends as f on u.id = f.user_id where u.id = ?";
    private static final String INSERT_INTO_USERS = "insert into users(email, login, name, birthday) " +
            "values(?, ?, ?, ?)";
    private static final String UPDATE_USERS = "update users " +
            "set email = ?, login = ?, name = ?, birthday = ? where id = ?";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserMaker userMaker) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMaker = userMaker;
    }

    @Override
    public Optional<List<User>> getAllUsers() {
        return Optional.of(jdbcTemplate.queryForStream(SELECT_ALL_USERS,
                        (rs, rowNum) -> userMaker.makeUser(rs))
                .findFirst()
                .orElseGet(ArrayList::new));
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(SELECT_USER_BY_ID,
                                (rs, rowNum) -> userMaker.makeUser(rs), id))
                .stream()
                        .findFirst())
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " doesn't exist " +
                        UserController.class.getSimpleName()));
    }

    @Override
    public Optional<User> createUser(@Valid User user) {
        userMaker.validateUser(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_INTO_USERS, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(@Valid User updatedUser) {
        int id = updatedUser.getId();
        getUserById(id).orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " doesn't exist " +
                UserController.class.getSimpleName()));
        userMaker.validateUser(updatedUser);
        int numUpdatedRow = jdbcTemplate.update(UPDATE_USERS,
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getName(),
                updatedUser.getBirthday(),
                updatedUser.getId());
        if (numUpdatedRow == 1) {
            return Optional.of(updatedUser);
        } else {
            throw new EmptyResultDataAccessException(EXPECTED_SIZE);
        }
    }
}