package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("userDbService")
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userDbStorage, UserService userDbService) {
        this.userStorage = userDbStorage;
        this.userService = userDbService;
    }

    @GetMapping
    public Optional<List<User>> getAllUsers() {
        log.info("Request to get all users");
        return userStorage.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Integer id) {
        log.info("Request to get a user by ID");
        return userStorage.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> showFriends(@PathVariable Integer id) {
        log.info("Request to display the user's friends");
        return userService.showFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> showCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Request to display common friends of users");
        return userService.showCommonFriends(id, otherId);
    }

    @PostMapping
    public Optional<User> createUser(@RequestBody User user) {
        log.info("Request to add a new user");
        return userStorage.createUser(user);
    }

    @PutMapping
    public Optional<User> updateUser(@RequestBody User updatedUser) {
        log.info("Request to update a user");
        return userStorage.updateUser(updatedUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Friend Request");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Request to remove from friends");
        userService.deleteFriend(id, friendId);
    }
}