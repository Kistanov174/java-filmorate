package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validation.Marker;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрос на получение всех фильмов");
        return inMemoryFilmStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.info("Запрос на получение фильма по ID");
        return inMemoryFilmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> showMostPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Запрос на получение самых популярных фильмов");
        return filmService.showMostPopularFilms(count);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Запрос на добавление нового фильма");
        return inMemoryFilmStorage.createFilm(film);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Film updateFilm(@RequestBody Film updatedfilm) {
        log.info("Запрос на обновление фильма");
        return inMemoryFilmStorage.updateFilm(updatedfilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Запрос на лайк фильма");
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Запрос на удаление лайка фильма");
        return filmService.deleteLike(id, userId);
    }
}