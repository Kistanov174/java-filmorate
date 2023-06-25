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
        log.info("Request to receive all movies");
        return inMemoryFilmStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.info("Request to receive a movie by ID");
        return inMemoryFilmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> showMostPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Request for the most popular movies");
        return filmService.showMostPopularFilms(count);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Request to create a new movie");
        return inMemoryFilmStorage.createFilm(film);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Film updateFilm(@RequestBody Film updatedfilm) {
        log.info("Movie Update Request");
        return inMemoryFilmStorage.updateFilm(updatedfilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Request to like the movie");
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Request to delete a movie like");
        return filmService.deleteLike(id, userId);
    }
}