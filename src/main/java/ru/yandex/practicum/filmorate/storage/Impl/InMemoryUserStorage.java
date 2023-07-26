package ru.yandex.practicum.filmorate.storage.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Slf4j
@Component("inMemoryUserStorage")
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private Integer count = 1;
    private final Map<Integer, User> users = new HashMap<>();
    private final Validator validator;

    private Integer generateId() {
        return count++;
    }

    @Override
    public Optional<List<User>> getAllUsers() {
        log.info(String.format("List of %d users has been received", users.size()));
        return Optional.of(new ArrayList<>(users.values()));
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("User with id = %d is absent", id));
        }
        User user = users.get(id);
        if (user == null) {
            throw new ObjectNotExistException(String.format("User with id = %d equals null", id));
        }
        log.info(String.format("User with id = %d has been received: ", id) + users.get(id));
        return Optional.of(user);
    }

    @Override
    public Optional<User> createUser(@Valid User user) {
        validateForCreate(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("User has been created: " + user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(@Valid User updatedUser) {
        validateForUpdate(updatedUser);
        User user = users.get(updatedUser.getId());
        user.setBirthday(updatedUser.getBirthday());
        user.setEmail(updatedUser.getEmail());
        user.setLogin(updatedUser.getLogin());
        user.setName(updatedUser.getName());
        log.info("User has been updated to: " + user);
        return Optional.of(updatedUser);
    }

    private void validateForUpdate(User user) {
        Integer id = user.getId();
        if (!users.containsKey(id)) {
            log.info(String.format("Film with id = %d doesn't exist", id));
            throw new ObjectNotExistException(user + " doesn't exist " + UserController.class.getSimpleName());
        }
        validateUser(user);
    }

    private void validateForCreate(User user) {
        Integer id = user.getId();
        if (users.containsKey(id)) {
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