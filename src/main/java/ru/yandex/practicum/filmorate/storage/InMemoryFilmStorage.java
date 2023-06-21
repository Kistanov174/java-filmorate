package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Integer count = 1;
    private static final int OLDEST_RELEASE_YEAR = 1895;
    private static final int OLDEST_RELEASE_MONTH = 12;
    private static final int OLDEST_RELEASE_DAY = 28;
    private final Map<Integer, Film> films = new HashMap<>();

    private Integer generateId() {
        return count++;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("Фильма с id = %d не существует", id));
        }
        log.info("Получен фильм " + films.get(id));
        return films.get(id);
    }

    @Override
    public Film createFilm(@Valid Film film) {
        RequestMethod requestMethod = RequestMethod.POST;
        doValidation(film, requestMethod);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм " + film);
        return film;
    }

    @Override
    public Film updateFilm(@Valid Film updatedfilm) {
        RequestMethod requestMethod = RequestMethod.PUT;
        doValidation(updatedfilm, requestMethod);
        Film film = films.get(updatedfilm.getId());
        film.setName(updatedfilm.getName());
        film.setReleaseDate(updatedfilm.getReleaseDate());
        film.setDuration(updatedfilm.getDuration());
        film.setDescription(updatedfilm.getDescription());
        log.info("Обновлен фильм" + updatedfilm);
        return updatedfilm;
    }

    private void doValidation(Film film, RequestMethod requestMethod) {
        if (requestMethod.equals(RequestMethod.PUT) && !films.containsKey(film.getId())) {
            log.info("Несуществующий фильм");
            throw new NullPointerException("Такого фильма нет" + FilmController.class.getSimpleName());
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