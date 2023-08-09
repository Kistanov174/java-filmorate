package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Optional<List<User>> getAllUsers() {
        log.info("Request to get all users");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Integer id) {
        log.info("Request to get a user by ID");
        return userService.findUserById(id);
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
        return userService.createUser(user);
    }

    @PutMapping
    public Optional<User> updateUser(@RequestBody User updatedUser) {
        log.info("Request to update a user");
        return userService.updateUser(updatedUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Optional<User> addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Friend Request");
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Optional<User>  deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Request to remove from friends");
        return userService.deleteFriend(id, friendId);
    }
}