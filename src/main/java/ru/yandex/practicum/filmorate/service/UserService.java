package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<List<User>> findAllUsers();

    Optional<User> findUserById(Integer id);

    Optional<User> createUser(User user);

    Optional<User> updateUser(User updatedUser);

    Optional<User> addFriend(Integer id, Integer friendId);

    Optional<User> deleteFriend(Integer id, Integer friendId);

    List<User> showCommonFriends(Integer id, Integer otherId);

    List<User> showFriends(Integer id);

    void checkUser(Integer id);
}
