package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final Validator validator;
    private static final int EXPECTED_SIZE = 1;

    @Override
    public Optional<List<User>> findAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return userDao.getUserById(id);
    }

    @Override
    public Optional<User> createUser(@Valid User user) {
        validateUser(user);
        Integer id = userDao.createUser(user).orElseThrow(() -> new EmptyResultDataAccessException(EXPECTED_SIZE));
        return userDao.getUserById(id);
    }

    @Override
    public Optional<User> updateUser(@Valid User updatedUser) {
        validateUser(updatedUser);
        int id = updatedUser.getId();
        userDao.getUserById(id).orElseThrow(() -> new ObjectNotFoundException("User with id = " + id +
                " doesn't exist " + UserServiceImpl.class.getSimpleName()));
        userDao.updateUser(updatedUser);
        return userDao.getUserById(updatedUser.getId());
    }

    @Override
    public Optional<User> addFriend(Integer id, Integer friendId) {
        checkUser(friendId);
        userDao.addFriend(id, friendId);
        return userDao.getUserById(id);
    }

    @Override
    public Optional<User> deleteFriend(Integer id, Integer friendId) {
        checkUser(friendId);
        userDao.deleteFriend(id, friendId);
        return userDao.getUserById(id);
    }

    @Override
    public List<User> showCommonFriends(Integer id, Integer otherId) {
        return userDao.showCommonFriends(id, otherId);
    }

    @Override
    public List<User> showFriends(Integer id) {
        return userDao.showFriends(id);
    }

    @Override
    public void checkUser(final Integer id) {
        Optional<User> friend = findUserById(id);
        friend.map(User::getId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " doesn't exist " +
                        UserServiceImpl.class.getSimpleName()));
    }

    private void validateUser(User user) {
        Set<ConstraintViolation<User>> constraintViolationSet = validator.validate(user);
        if (!CollectionUtils.isEmpty(constraintViolationSet)) {
            log.debug("Validation failed - " + constraintViolationSet);
            throw new ValidationException("Validation failed " + constraintViolationSet);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Name is absent, login is used");
        }
    }
}