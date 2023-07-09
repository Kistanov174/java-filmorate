package ru.yandex.practicum.filmorate.storage.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Validator validator;
    String sql = "select id from users";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, Validator validator) {
        this.jdbcTemplate = jdbcTemplate;
        this.validator = validator;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "select * from users as u left join friends as f on u.id = f.user_id order by u.id";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        String sql = "select * from users as u left join friends as f on u.id = f.user_id where u.id = ?";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id)).stream().findFirst();
    }

    @Override
    public Optional<User> createUser(@Valid User user) {
        validateForCreate(user);
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
        validateForUpdate(updatedUser);
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";
        jdbcTemplate.update(sql,
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getName(),
                updatedUser.getBirthday(),
                updatedUser.getId());
        return getUserById(Objects.requireNonNull(updatedUser.getId()));
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

    private void validateForUpdate(User user) {
        Integer id = user.getId();
        if (!jdbcTemplate.queryForList(sql).contains(id)) {
            log.info(String.format("Film with id = %d doesn't exist", id));
            throw new ObjectNotFoundException(user + " doesn't exist " + UserController.class.getSimpleName());
        }
        validateUser(user);
    }

    private void validateForCreate(User user) {
        Integer id = user.getId();
        if (jdbcTemplate.queryForList(sql).contains(id)) {
            log.info(String.format("Film with id = %d doesn't exist", id));
            throw new ValidationException(user + " is already exist " + UserController.class.getSimpleName());
        }
        validateUser(user);
    }

    private void validateUser(User user) {
        Set<ConstraintViolation<User>> constraintViolationSet = validator.validate(user);
        if (!CollectionUtils.isEmpty(constraintViolationSet)) {
            log.info("Validation failed - " + constraintViolationSet);
            throw new ValidationException("Validation failed " + constraintViolationSet);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Name is absent, login is used");
        }
    }
}