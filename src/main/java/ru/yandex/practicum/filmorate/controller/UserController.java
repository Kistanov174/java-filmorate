package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    Integer count = 1;
    private final Map<Integer, User> users = new HashMap<>();

    private Integer generateId() {
        return count++;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на добавление нового пользователя");
        doValidation(user);
        user.setId(generateId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не указано, использован логин");
        }
        users.put(user.getId(), user);
        log.info("Создан пользователь " + user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя");
        doValidation(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не указано, использован логин");
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь " + user);
        return user;
    }

    private void doValidation(User user) {
        if (user.getEmail().isBlank()) {
            log.info("Пустой email-адрес");
            throw new ValidationException("Пустой email-адрес в запросе " + UserController.class.getSimpleName());
        }
        if (!user.getEmail().contains("@")) {
            log.info("Неправильный формат email");
            throw new ValidationException("Неправильный формат email в запросе " + UserController.class.getSimpleName());
        }
        if (user.getLogin().isBlank()) {
            log.info("Пустой login");
            throw new ValidationException("Пустой login в запросе " + UserController.class.getSimpleName());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Дата рождения еще не наступила");
            throw new ValidationException("Дата рождения еще не наступила в запросе " +
                    UserController.class.getSimpleName());
        }
    }
}