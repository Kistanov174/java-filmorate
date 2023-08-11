package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreDao genreDao;

    @Override
    public Genre findGenreById(Integer id) {
        return genreDao.getGenreById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Genre with id = " + id + " hasn't found"));
    }

    @Override
    public List<Genre> findAllGenres() {
        return genreDao.getAllGenres()
                .orElseThrow(() -> new ObjectNotFoundException("Genres haven't found"));
    }
}
