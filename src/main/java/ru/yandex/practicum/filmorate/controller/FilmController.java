package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Marker;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Optional<List<Film>> getAllFilms() {
        log.info("Request to receive all movies");
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Optional<Film> getFilmById(@PathVariable Integer id) {
        log.info("Request to receive a movie by ID");
        return filmService.findFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> showMostPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Request for the most popular movies");
        return filmService.showMostPopularFilms(count);
    }

    @PostMapping
    public Optional<Film> createFilm(@RequestBody Film film) {
        log.info("Request to create a new movie");
        return filmService.createFilm(film);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Optional<Film> updateFilm(@RequestBody Film updatedfilm) {
        log.info("Movie Update Request");
        return filmService.updateFilm(updatedfilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Optional<Film> addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Request to like the movie");
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Optional<Film> deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Request to delete a movie like");
        return filmService.deleteLike(id, userId);
    }
}