package ru.yandex.practicum.filmorate.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMaker {
    private final Validator validator;

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
