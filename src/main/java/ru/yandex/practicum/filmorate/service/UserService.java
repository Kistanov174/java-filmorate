package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> addFriend(Integer id, Integer friendId);

    Optional<User> deleteFriend(Integer id, Integer friendId);

    List<User> showCommonFriends(Integer id, Integer otherId);

    List<User> showFriends(Integer id);
}