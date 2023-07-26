package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.Impl.FilmDbService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.Marker;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmDbStorage;
    private final FilmDbService filmDbService;

    @GetMapping
    public Optional<List<Film>> getAllFilms() {
        log.info("Request to receive all movies");
        return filmDbStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Optional<Film> getFilmById(@PathVariable Integer id) {
        log.info("Request to receive a movie by ID");
        return filmDbStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> showMostPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Request for the most popular movies");
        return filmDbService.showMostPopularFilms(count);
    }

    @PostMapping
    public Optional<Film> createFilm(@RequestBody Film film) {
        log.info("Request to create a new movie");
        return filmDbStorage.createFilm(film);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Optional<Film> updateFilm(@RequestBody Film updatedfilm) {
        log.info("Movie Update Request");
        return filmDbStorage.updateFilm(updatedfilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Request to like the movie");
        filmDbService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Request to delete a movie like");
        filmDbService.deleteLike(id, userId);
    }
}