package ru.yandex.practicum.filmorate.dao.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserDao;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final Validator validator;
    private static final int EXPECTED_SIZE = 1;
    private static final String SELECT_ALL_USERS = "select * " +
            "from users as u left join friends as f on u.id = f.user_id order by u.id";
    private static final String SELECT_USER_BY_ID = "select * " +
            "from users as u left join friends as f on u.id = f.user_id where u.id = ?";
    private static final String INSERT_INTO_USERS = "insert into users(email, login, name, birthday) " +
            "values(?, ?, ?, ?)";
    private static final String UPDATE_USERS = "update users " +
            "set email = ?, login = ?, name = ?, birthday = ? where id = ?";
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
    public Optional<List<User>> getAllUsers() {
        return Optional.of(jdbcTemplate.queryForStream(SELECT_ALL_USERS,
                        (rs, rowNum) -> makeUser(rs))
                .findFirst()
                .orElseGet(ArrayList::new));
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(SELECT_USER_BY_ID,
                                (rs, rowNum) -> makeUser(rs), id))
                .stream()
                        .findFirst())
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " doesn't exist " +
                        UserController.class.getSimpleName()));
    }

    @Override
    public Optional<User> createUser(@Valid User user) {
        validateUser(user);
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
    public void updateUser(@Valid User updatedUser) {
        int id = updatedUser.getId();
        getUserById(id).orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " doesn't exist " +
                UserController.class.getSimpleName()));
        validateUser(updatedUser);
        int numUpdatedRow = jdbcTemplate.update(UPDATE_USERS,
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getName(),
                updatedUser.getBirthday(),
                updatedUser.getId());
        if (numUpdatedRow != 1) {
            throw new EmptyResultDataAccessException(EXPECTED_SIZE);
        }
    }

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
                (rs, rowNum) -> makeUser(rs), id, otherId).stream().findFirst().orElseGet(ArrayList::new);
    }

    @Override
    public List<User> showFriends(Integer id) {
        return jdbcTemplate.query(SELECT_FRIENDS,
                (rs, rowNum) -> makeUser(rs), id).stream().findFirst().orElseGet(ArrayList::new);
    }

    public Integer checkUser(final Integer friendId) {
        Optional<User> friend = getUserById(friendId);
        return friend.map(User::getId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + friendId + " doesn't exist " +
                        UserDaoImpl.class.getSimpleName()));
    }

    public List<User> makeUser(ResultSet rs) throws SQLException {
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

    public void validateUser(User user) {
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