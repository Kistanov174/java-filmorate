package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<List<User>> getAllUsers();

    Optional<User> getUserById(Integer id);

    Optional<User> createUser(User user);

    Optional<User> updateUser(User updatedUser);
}