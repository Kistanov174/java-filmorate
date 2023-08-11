package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmService {
    List<Film> findAllFilms();

    Film findFilmById(Integer id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film addLike(Integer id, Integer userId);

    Film deleteLike(Integer id, Integer userId);

    List<Film> showMostPopularFilms(Integer count);
}