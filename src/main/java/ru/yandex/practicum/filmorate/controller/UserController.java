package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.yandex.practicum.filmorate.validation.Marker;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private Integer count = 1;
    private final Map<Integer, User> users = new HashMap<>();

    private Integer generateId() {
        return count++;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на добавление нового пользователя");
        RequestMethod requestMethod = RequestMethod.POST;
        doValidation(user, requestMethod);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Создан пользователь " + user);
        return user;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Запрос на обновление пользователя");
        RequestMethod requestMethod = RequestMethod.PUT;
        doValidation(updatedUser, requestMethod);
        User user = users.get(updatedUser.getId());
        user.setBirthday(updatedUser.getBirthday());
        user.setEmail(updatedUser.getEmail());
        user.setLogin(updatedUser.getLogin());
        user.setName(updatedUser.getName());
        log.info("Обновлен пользователь " + user);
        return updatedUser;
    }

    private void doValidation(User user, RequestMethod requestMethod) {
        if (requestMethod.equals(RequestMethod.PUT) && !users.containsKey(user.getId())) {
            log.info("Несуществующий пользователь");
            throw new ValidationException("Такого пользователтя нет" + UserController.class.getSimpleName());
        }
        if (requestMethod.equals(RequestMethod.POST) && users.containsKey(user.getId())) {
            log.info("Такой пользователь уже создан");
            throw new ValidationException("Такой пользователь уже существует" + UserController.class.getSimpleName());
        }
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
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не указано, использован логин");
        }
    }
}