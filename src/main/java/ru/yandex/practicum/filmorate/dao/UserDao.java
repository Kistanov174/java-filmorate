package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<List<User>> getAllUsers();

    Optional<User> getUserById(Integer id);

    Optional<Integer> createUser(User user);

    void updateUser(User updatedUser);

    void addFriend(Integer id, Integer friendId);

    void deleteFriend(Integer id, Integer friendId);

    List<User> showCommonFriends(Integer id, Integer otherId);

    List<User> showFriends(Integer id);
}