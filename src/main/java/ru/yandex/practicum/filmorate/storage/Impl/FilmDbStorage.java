package ru.yandex.practicum.filmorate.storage.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ObjectNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Validator validator;
    String sql = "select id from films";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, Validator validator) {
        this.jdbcTemplate = jdbcTemplate;
        this.validator = validator;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "select * from films as f left join film_likes as fl on f.id = fl.film_id " +
                "left join film_genre as fg on f.id = fg.film_id";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        String sql = "select * " +
                "from films as f left join film_likes as fl on f.id = fl.film_id " +
                "left join film_genre as fg on f.id = fg.film_id where f.id = ?";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id))
                        .stream().findFirst();
    }

    @Override
    public Optional<Film> createFilm(@Valid Film film) {
        validateForCreate(film);
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
        return getFilmById(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public Optional<Film> updateFilm(@Valid Film film) {
        validateForUpdate(film);
        String sql = "update films set name = ?, description = ?, release_Date = ?, duration = ?, mpa_Id = ?" +
                " where id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa());
        return getFilmById(Objects.requireNonNull(film.getId()));
    }

    private List<Film> makeFilm(ResultSet rs) throws SQLException {
        List<Film> films = new ArrayList<>();
        do {
            Film film = new Film();
            Mpa mpa = new Mpa();
            film.setId(rs.getInt("id"));
            film.setDescription(rs.getString("description"));
            film.setName(rs.getString("name"));
            film.setDuration(rs.getInt("duration"));
            film.setReleaseDate(rs.getDate("release_Date").toLocalDate());
            mpa.setId(rs.getInt("mpa_id"));
            film.setMpa(mpa);
            do {
                Genre genre = new Genre();
                genre.setId(rs.getInt("genre_id"));
                film.getGenres().add(genre);
                if (rs.getInt("user_id") != 0) {
                    film.likes.add(rs.getInt("user_id"));
                }
            } while (rs.next() && rs.getInt("id") == film.getId());
            films.add(film);
            film.setRate(film.getLikes().size());
        } while (!rs.isAfterLast());
        return films;
    }

    private void validateForUpdate(Film film) {
        Integer id = film.getId();
        if (!jdbcTemplate.queryForList(sql).contains(id)) {
            log.info(String.format("Film with id = %d doesn't exist", id));
            throw new ObjectNotExistException(film + " doesn't exist " + FilmController.class.getSimpleName());
        }
        validateFilm(film);
    }

    private void validateForCreate(Film film) {
        Integer id = film.getId();
        if (!jdbcTemplate.queryForList(sql).contains(id)) {
            log.info(String.format("Film with id = %d doesn't exist", id));
            throw new ValidationException(film + " is already exist " + FilmController.class.getSimpleName());
        }
        validateFilm(film);
    }

    private void validateFilm(Film film) {
        Set<ConstraintViolation<Film>> constraintViolationSet = validator.validate(film);
        if (!CollectionUtils.isEmpty(constraintViolationSet)) {
            log.info("Validation failed - " + constraintViolationSet);
            throw new ValidationException("Validation failed " + constraintViolationSet);
        }
    }
}