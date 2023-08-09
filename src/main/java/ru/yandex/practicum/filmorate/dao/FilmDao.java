package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

public interface FilmDao {
    Optional<List<Film>> getAllFilms();

    Optional<Film> getFilmById(Integer id);

    Optional<Integer> createFilm(Film film);

    void updateFilm(Film film);

    void addLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    List<Film> showMostPopularFilms(Integer count);
}