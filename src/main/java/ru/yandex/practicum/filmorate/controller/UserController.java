package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Request to get all users");
        return inMemoryUserStorage.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Request to get a user by ID");
        return inMemoryUserStorage.getUserById(id);
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
    public User createUser(@RequestBody User user) {
        log.info("Request to add a new user");
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User updatedUser) {
        log.info("Request to update a user");
        return inMemoryUserStorage.updateUser(updatedUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Friend Request");
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Request to remove from friends");
        return userService.deleteFriend(id, friendId);
    }
}