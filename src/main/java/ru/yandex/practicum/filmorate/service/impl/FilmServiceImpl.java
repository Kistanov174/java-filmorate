package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmDao filmDao;
    private final UserService userService;
    private final Validator validator;
    private static final int EXPECTED_SIZE = 1;

    @Override
    public Optional<List<Film>> findAllFilms() {
        return filmDao.getAllFilms();
    }

    @Override
    public Optional<Film> findFilmById(Integer id) {
        return filmDao.getFilmById(id);
    }

    @Override
    public Optional<Film> createFilm(@Valid Film film) {
        validateFilm(film);
        Integer id = filmDao.createFilm(film).orElseThrow(() -> new EmptyResultDataAccessException(EXPECTED_SIZE));
        return filmDao.getFilmById(id);
    }

    @Override
    public Optional<Film> updateFilm(@Valid Film film) {
        Integer id = film.getId();
        filmDao.getFilmById(id).orElseThrow(() -> new ObjectNotFoundException("Film with id = " + id +
                " doesn't exist " + FilmController.class.getSimpleName()));
        validateFilm(film);
        filmDao.updateFilm(film);
        return filmDao.getFilmById(film.getId());
    }

    @Override
    public Optional<Film> addLike(Integer id, Integer userId) {
        userService.checkUser(userId);
        filmDao.addLike(id, userId);
        return filmDao.getFilmById(id);
    }

    @Override
    public Optional<Film> deleteLike(Integer id, Integer userId) {
        userService.checkUser(userId);
        filmDao.deleteLike(id, userId);
        return filmDao.getFilmById(id);
    }

    @Override
    public List<Film> showMostPopularFilms(Integer count) {
        return filmDao.showMostPopularFilms(count);
    }

    private void validateFilm(Film film) {
        Set<ConstraintViolation<Film>> constraintViolationSet = validator.validate(film);
        if (!CollectionUtils.isEmpty(constraintViolationSet)) {
            log.debug("Validation failed - " + constraintViolationSet);
            throw new ValidationException("Validation failed " + constraintViolationSet);
        }
    }
}