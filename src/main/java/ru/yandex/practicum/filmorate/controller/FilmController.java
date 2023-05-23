package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    Integer count = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    private Integer generateId() {
        return count++;
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping("/film")
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление нового фильма");
        doValidation(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм " + film);
        return film;
    }

    @PutMapping("/film")
    public Film update(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление фильма");
        doValidation(film);
        films.put(film.getId(), film);
        log.info("Добавлен фильм " + film);
        return film;
    }

    private void doValidation(Film film) {
        if (film.getName().isBlank() ) {
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
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза до 28.12.1895 года");
            throw new ValidationException("Слишком старая дата релиза в запросе " +
                    FilmController.class.getSimpleName());
        }
        if (film.getDuration() != null && film.getDuration().toMinutes() < 0) {
            log.info("Отрицательная продолжительность фильма");
            throw new ValidationException("Отрицательная продолжительность в запросе " +
                    FilmController.class.getSimpleName());
        }
    }
}