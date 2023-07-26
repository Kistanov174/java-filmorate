package ru.yandex.practicum.filmorate.storage.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.Impl.FilmMaker;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import javax.validation.Valid;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.Optional;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMaker filmMaker;
    private static final String SELECT_ALL_FILMS = "select * " +
            "from films as f left join film_likes as fl on f.id = fl.film_id " +
            "left join film_genre as fg on f.id = fg.film_id " +
            "left join genres as g on fg.genre_id = g.genre_id " +
            "left join mpa_ratings as mr on f.mpa_id = mr.mpa_id ";
    private static final String SELECT_FILM_BY_ID = "select * " +
            "from films as f left join film_likes as fl on f.id = fl.film_id " +
            "left join film_genre as fg on f.id = fg.film_id " +
            "left join genres as g on fg.genre_id = g.genre_id " +
            "left join mpa_ratings as mr on f.mpa_id = mr.mpa_id " +
            "where f.id = ?";
    private static final String INSERT_INTO_FILMS = "insert into films(name, description, " +
            "release_date, duration, mpa_Id) " +
            "values(?, ?, ?, ?, ?)";
    private static final String UPDATE_FILMS = "update films set name = ?, description = ?, " +
            "release_Date = ?, duration = ?, mpa_Id = ? " +
            "where id = ?";
    private static final String DELETE_FROM_FILM_GENRE = "delete from film_genre where film_id = ?";
    private static final String INSERT_INTO_FILM_GENRE = "insert into film_genre values(?, ?)";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMaker filmMaker) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMaker = filmMaker;
    }

    @Override
    public Optional<List<Film>> getAllFilms() {
        return Optional.of(jdbcTemplate.queryForStream(SELECT_ALL_FILMS, (rs, rowNum) -> filmMaker.makeFilm(rs))
                .findFirst()
                .orElseGet(ArrayList::new));
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(SELECT_FILM_BY_ID,
                                (rs, rowNum) -> filmMaker.makeFilm(rs), id))
                .stream()
                        .findFirst())
                .orElseThrow(() -> new ObjectNotFoundException("Film with id = " + id + " doesn't exist." +
                        FilmController.class.getSimpleName()));
    }

    @Override
    public Optional<Film> createFilm(@Valid Film film) {
        filmMaker.validateFilm(film);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_INTO_FILMS, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        updateGenre(Objects.requireNonNull(keyHolder.getKey()).intValue(), film.getGenres());
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateFilm(@Valid Film film) {
        Integer id = film.getId();
        getFilmById(id).orElseThrow(() -> new ObjectNotFoundException("Film with id = " + id + " doesn't exist " +
                FilmController.class.getSimpleName()));
        filmMaker.validateFilm(film);
        jdbcTemplate.update(UPDATE_FILMS,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        updateGenre(id, film.getGenres());
        return getFilmById(id);
    }

    private void updateGenre(int filmId, Collection<Genre> genreId) {
        jdbcTemplate.update(DELETE_FROM_FILM_GENRE, filmId);
        for (Genre genre : genreId) {
            jdbcTemplate.update(INSERT_INTO_FILM_GENRE, filmId, genre.getId());
        }
    }
}