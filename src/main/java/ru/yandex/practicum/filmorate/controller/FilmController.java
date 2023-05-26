package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.sirvice.Marker;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController("/ru/yandex/practicum/filmorate/controller/FilmController")
public class FilmController {
    private Integer count = 1;
    private static final int OLDEST_RELEASE_YEAR = 1895;
    private static final int OLDEST_RELEASE_MONTH = 12;
    private static final int OLDEST_RELEASE_DAY = 28;
    private final Map<Integer, Film> films = new HashMap<>();

    private Integer generateId() {
        return count++;
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        log.info("Запрос на получение всех фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    @Validated({Marker.OnCreate.class})
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление нового фильма");
        RequestMethod requestMethod = RequestMethod.POST;
        doValidation(film, requestMethod);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм " + film);
        return film;
    }

    @PutMapping("/films")
    @Validated({Marker.OnUpdate.class})
    public Film update(@Valid @RequestBody Film updatedfilm) {
        log.info("Запрос на обновление фильма");
        RequestMethod requestMethod = RequestMethod.PUT;
        doValidation(updatedfilm, requestMethod);
        Film film = films.get(updatedfilm.getId());
        film.setName(updatedfilm.getName());
        film.setReleaseDate(updatedfilm.getReleaseDate());
        film.setDuration(updatedfilm.getDuration());
        film.setDescription(updatedfilm.getDescription());
        log.info("Обновлен фильм" + film);
        return updatedfilm;
    }

    private void doValidation(Film film, RequestMethod requestMethod) {
        if (requestMethod.equals(RequestMethod.PUT) && !films.containsKey(film.getId())) {
            log.info("Несуществующий фильм");
            throw new ValidationException("Такого фильма нет" + FilmController.class.getSimpleName());
        }
        if (requestMethod.equals(RequestMethod.POST) && films.containsKey(film.getId())) {
            log.info("Такой фильм уже есть");
            throw new ValidationException("Такой фильм уже существует" + FilmController.class.getSimpleName());
        }
        if (film.getName().isBlank()) {
            log.info("Пустое имя фильма");
            throw new ValidationException("Пустое имя фильма в запросе " +
                    FilmController.class.getSimpleName());
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.info("Описание фильма больше 200 символов");
            throw new ValidationException("Слишком длинное описание фильма в запросе " +
                    FilmController.class.getSimpleName());
        }
        if (film.getReleaseDate() != null &&
                film.getReleaseDate().isBefore(LocalDate.of(OLDEST_RELEASE_YEAR, OLDEST_RELEASE_MONTH,
                        OLDEST_RELEASE_DAY))) {
            log.info("Дата релиза до 28.12.1895 года");
            throw new ValidationException("Слишком старая дата релиза в запросе " +
                    FilmController.class.getSimpleName());
        }
        if (film.getDuration() != null && film.getDuration() < 0) {
            log.info("Отрицательная продолжительность фильма");
            throw new ValidationException("Отрицательная продолжительность в запросе " +
                    FilmController.class.getSimpleName());
        }
    }
}