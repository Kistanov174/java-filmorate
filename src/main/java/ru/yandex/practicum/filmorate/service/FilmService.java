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
        log.info(String.format("The film with id = %d has been like by user with id = %d", id, userId));
        return filmStorage.getFilmById(id);
    }

    public Film deleteLike(Integer id, Integer userId) {
        if (!filmStorage.getFilmById(id).likes.contains(userId)) {
            throw new ObjectNotFoundException(String.format("The film with id = %d doesn't have like from user with" +
                    " id = %d", id, userId));
        }
        filmStorage.getFilmById(id).likes.remove(userId);
        log.info(String.format("The movie with id = %d has been unliked by user with id = %d", id, userId));
        return filmStorage.getFilmById(id);
    }

    public List<Film> showMostPopularFilms(Integer count) {
        if (count <= 0 || count == null) {
            throw new ValidationException(String.format("Wrong format for count = %d", count));
        }
        if (count > filmStorage.getAllFilms().size()) {
            count = filmStorage.getAllFilms().size();
        }
        log.info(String.format("The list of the most popular of %d films has been received", count));
        return filmStorage.getAllFilms().stream()
                .sorted((film1, film2) -> film2.likes.size() - film1.likes.size())
                .collect(Collectors.toList()).subList(0, count);
    }
}