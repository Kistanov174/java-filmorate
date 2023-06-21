package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(Integer id, Integer userId) {
        filmStorage.getFilmById(id).likes.add(userId);
        log.info("Поставлен лайк фильму");
        return filmStorage.getFilmById(id);
    }

    public Film deleteLike(Integer id, Integer userId) {
        if (!filmStorage.getFilmById(id).likes.contains(userId)) {
            throw new ObjectNotFoundException(String.format("У фильма нет лайка от пользователя с id = %d", userId));
        }
        filmStorage.getFilmById(id).likes.remove(userId);
        log.info("Удален лайк фильму");
        return filmStorage.getFilmById(id);
    }

    public List<Film> showMostPopularFilms(Integer count) {
        if (count <= 0) {
            throw new ValidationException("Неверный формат параметра count");
        }
        if (filmStorage.getAllFilms().size() < count) {
            count = filmStorage.getAllFilms().size();
        }
        log.info("Получен список самых популярных фильмов");
        return filmStorage.getAllFilms().stream()
                .sorted((film1, film2) -> film2.likes.size() - film1.likes.size())
                .collect(Collectors.toList()).subList(0, count);
    }
}