package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserService {
    List<User> findAllUsers();

    User findUserById(Integer id);

    User createUser(User user);

    User updateUser(User updatedUser);

    User addFriend(Integer id, Integer friendId);

    User deleteFriend(Integer id, Integer friendId);

    List<User> showCommonFriends(Integer id, Integer otherId);

    List<User> showFriends(Integer id);

    void checkUser(Integer id);
}
