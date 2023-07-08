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
import java.util.*;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMaker filmMaker;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMaker filmMaker) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMaker = filmMaker;
    }

    @Override
    public Optional<List<Film>> getAllFilms() {
        String sql = "select * " +
                "from films as f left join film_likes as fl on f.id = fl.film_id " +
                "left join film_genre as fg on f.id = fg.film_id " +
                "left join genres as g on fg.genre_id = g.genre_id " +
                "left join mpa_ratings as mr on f.mpa_id = mr.mpa_id ";
        return Optional.of(jdbcTemplate.queryForStream(sql, (rs, rowNum) -> filmMaker.makeFilm(rs)).findFirst()
                .orElseGet(ArrayList::new));
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        String sql = "select * " +
                "from films as f left join film_likes as fl on f.id = fl.film_id " +
                "left join film_genre as fg on f.id = fg.film_id " +
                "left join genres as g on fg.genre_id = g.genre_id " +
                "left join mpa_ratings as mr on f.mpa_id = mr.mpa_id " +
                "where f.id = ?";
        return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(sql,
                                (rs, rowNum) -> filmMaker.makeFilm(rs), id))
                .stream()
                        .findFirst())
                .orElseThrow(() -> new ObjectNotFoundException("Film with id = " + id + " doesn't exist." +
                        FilmController.class.getSimpleName()));
    }

    @Override
    public Optional<Film> createFilm(@Valid Film film) {
        filmMaker.validateFilm(film);
        String sql = "insert into films(name, description, release_date, duration, mpa_Id)" +
                " values(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        updateGenre(Objects.requireNonNull(keyHolder.getKey()).intValue(), film.getGenres());
        return getFilmById((Objects.requireNonNull(keyHolder.getKey())).intValue());
    }

    @Override
    public Optional<Film> updateFilm(@Valid Film film) {
        Integer id = film.getId();
        getFilmById(id).orElseThrow(() -> new ObjectNotFoundException("Film with id = " + id + " doesn't exist " +
                FilmController.class.getSimpleName()));
        filmMaker.validateFilm(film);
        String sql = "update films set name = ?, description = ?, release_Date = ?, duration = ?, mpa_Id = ?" +
                " where id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        updateGenre(film.getId(), film.getGenres());
        return getFilmById(Objects.requireNonNull(film.getId()));
    }

    private void updateGenre(int filmId, Collection<Genre> genreId) {
        String sqlDelete = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlDelete, filmId);
        for (Genre genre : genreId) {
            String sqlAdd = "insert into film_genre values(?, ?)";
            jdbcTemplate.update(sqlAdd, filmId, genre.getId());
        }
    }
}