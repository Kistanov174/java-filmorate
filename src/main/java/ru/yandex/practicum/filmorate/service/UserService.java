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
        log.info(String.format("User with id = %d has been added to the user's friends with id = %d", friendId, id));
        return userStorage.getUserById(id);
    }

    public User deleteFriend(Integer id, Integer friendId) {
        if (!userStorage.getUserById(id).friends.contains(friendId)) {
            throw new ObjectNotFoundException(String.format("The user doesn't have friend with id = %d", friendId));
        }
        userStorage.getUserById(id).friends.remove(friendId);
        userStorage.getUserById(friendId).friends.remove(id);
        log.info(String.format("User with id = %d has been deleted from the user's friends with id = %d", friendId, id));
        return userStorage.getUserById(id);
    }

    public List<User> showCommonFriends(Integer id, Integer otherId) {
        log.info(String.format("The common friends of the users with id = %d and %d have been showed", id, otherId));
        return userStorage.getUserById(id).friends.stream()
                .filter(userStorage.getUserById(otherId).friends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> showFriends(Integer id) {
        log.info(String.format("The user's friends with id = %d have been showed", id));
        return userStorage.getUserById(id).friends.stream()
                .filter(x -> x >= 0)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}