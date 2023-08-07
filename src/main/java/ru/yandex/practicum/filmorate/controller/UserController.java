package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserDao;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserDao userDao;

    @GetMapping
    public Optional<List<User>> getAllUsers() {
        log.info("Request to get all users");
        return userDao.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Integer id) {
        log.info("Request to get a user by ID");
        return userDao.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> showFriends(@PathVariable Integer id) {
        log.info("Request to display the user's friends");
        return userDao.showFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> showCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Request to display common friends of users");
        return userDao.showCommonFriends(id, otherId);
    }

    @PostMapping
    public Optional<User> createUser(@RequestBody User user) {
        log.info("Request to add a new user");
        return userDao.createUser(user);
    }

    @PutMapping
    public Optional<User> updateUser(@RequestBody User updatedUser) {
        log.info("Request to update a user");
        userDao.updateUser(updatedUser);
        return userDao.getUserById(updatedUser.getId());
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Friend Request");
        userDao.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Request to remove from friends");
        userDao.deleteFriend(id, friendId);
    }
}