package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Impl.GenreDbStorage;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreDbStorage genreDbStorage;

    @GetMapping
    public Optional<List<Genre>> getAllGenres() {
        log.info("Request to get all genres");
        return genreDbStorage.getAllGenres();
    }

    @GetMapping("/{id}")
    public Optional<Genre> getGenreById(@PathVariable Integer id) {
        log.info("Request to get genre by id");
        return genreDbStorage.getGenreById(id);
    }
}