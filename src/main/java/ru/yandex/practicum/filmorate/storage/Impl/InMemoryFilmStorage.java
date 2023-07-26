package ru.yandex.practicum.filmorate.storage.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ObjectNotExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Slf4j
@Component("inMemoryFilmStorage")
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private Integer count = 1;
    private static final int OLDEST_RELEASE_YEAR = 1895;
    private static final int OLDEST_RELEASE_MONTH = 12;
    private static final int OLDEST_RELEASE_DAY = 28;
    private final Map<Integer, Film> films = new HashMap<>();
    private final Validator validator;

    private Integer generateId() {
        return count++;
    }

    @Override
    public Optional<List<Film>> getAllFilms() {
        log.info(String.format("List of %d film has been received", films.size()));
        return Optional.of(new ArrayList<>(films.values()));
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("The film with id = %d not found", id));
        }
        Film film = films.get(id);
        if (film == null) {
            throw new ObjectNotExistException(String.format("Film with id = %d equals null", id));
        }
        log.info("The film has been received: " + film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> createFilm(@Valid Film film) {
        validateForCreate(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Film has been created: " + film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateFilm(@Valid Film updatedfilm) {
        validateForUpdate(updatedfilm);
        Film film = films.get(updatedfilm.getId());
        film.setName(updatedfilm.getName());
        film.setReleaseDate(updatedfilm.getReleaseDate());
        film.setDuration(updatedfilm.getDuration());
        film.setDescription(updatedfilm.getDescription());
        log.info(String.format("Film with id = %d has been updated to: ", film.getId()) + updatedfilm);
        return Optional.of(updatedfilm);
    }

    private void validateForUpdate(Film film) {
        Integer id = film.getId();
        if (!films.containsKey(id)) {
            log.info(String.format("Film with id = %d doesn't exist", id));
            throw new ObjectNotExistException(film + " doesn't exist " + FilmController.class.getSimpleName());
        }
        validateFilm(film);
    }

    private void validateForCreate(Film film) {
        Integer id = film.getId();
        if (films.containsKey(id)) {
            log.info(String.format("Film with id = %d doesn't exist", id));
            throw new ValidationException(film + " is already exist " + FilmController.class.getSimpleName());
        }
        validateFilm(film);
    }

    private void validateFilm(Film film) {
        Set<ConstraintViolation<Film>> constraintViolationSet = validator.validate(film);
        if (!CollectionUtils.isEmpty(constraintViolationSet)) {
            log.info("Validation failed - " + constraintViolationSet);
            throw new ValidationException("Validation failed " + constraintViolationSet);
        }
    }
}