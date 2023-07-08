package ru.yandex.practicum.filmorate.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmMaker {
    private final Validator validator;

    public List<Film> makeFilm(ResultSet rs) throws SQLException {
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
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
            do {
                Genre genre = new Genre();
                int genreId = rs.getInt("genre_id");
                if (genreId != 0) {
                    genre.setId(genreId);
                    genre.setName(rs.getString("genre_name"));
                    film.getGenres().add(genre);
                }
                if (rs.getInt("user_id") != 0) {
                    film.likes.add(rs.getInt("user_id"));
                }
            } while (rs.next() && rs.getInt("id") == film.getId());
            films.add(film);
            film.setRate(film.getLikes().size());
        } while (!rs.isAfterLast());
        return films;
    }

    public void validateFilm(Film film) {
        Set<ConstraintViolation<Film>> constraintViolationSet = validator.validate(film);
        if (!CollectionUtils.isEmpty(constraintViolationSet)) {
            log.info("Validation failed - " + constraintViolationSet);
            throw new ValidationException("Validation failed " + constraintViolationSet);
        }
    }
}