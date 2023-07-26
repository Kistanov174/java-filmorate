package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmService {
    void addLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    List<Film> showMostPopularFilms(Integer count);
}
