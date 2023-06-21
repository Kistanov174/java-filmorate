package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Integer id, Integer friendId) {
        userStorage.getUserById(id).friends.add(friendId);
        userStorage.getUserById(friendId).friends.add(id);
        log.info("Произведено добавление в друзья");
        return userStorage.getUserById(id);
    }

    public User deleteFriend(Integer id, Integer friendId) {
        if (!userStorage.getUserById(id).friends.contains(friendId)) {
            throw new ObjectNotFoundException(String.format("У пользователя нет друга с id = %d", friendId));
        }
        userStorage.getUserById(id).friends.remove(friendId);
        userStorage.getUserById(friendId).friends.remove(id);
        log.info("Произведено удаление из друзей");
        return userStorage.getUserById(id);
    }

    public List<User> showCommonFriends(Integer id, Integer otherId) {
        log.info("Отображены общие друзья");
        return userStorage.getUserById(id).friends.stream()
                .filter(userStorage.getUserById(otherId).friends::contains)
                .map(x -> userStorage.getUserById(x))
                .collect(Collectors.toList());
    }

    public List<User> showFriends(Integer id) {
        log.info("Отображены друзья");
        return userStorage.getUserById(id).friends.stream()
                .filter(x -> x >= 0)
                .map(x -> userStorage.getUserById(x))
                .collect(Collectors.toList());
    }
}