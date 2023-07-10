package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

public interface FilmService {
    Optional<Film> addLike(Integer id, Integer userId);

    Optional<Film> deleteLike(Integer id, Integer userId);

    List<Film> showMostPopularFilms(Integer count);
}
