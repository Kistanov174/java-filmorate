package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<List<Film>> getAllFilms();

    Optional<Film> getFilmById(Integer id);

    Optional<Film> createFilm(Film film);

    Optional<Film> updateFilm(Film film);
}