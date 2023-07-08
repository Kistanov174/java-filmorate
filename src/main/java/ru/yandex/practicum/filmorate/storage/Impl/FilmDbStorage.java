package ru.yandex.practicum.filmorate.storage.Impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
    public Optional<Film> createFilm(Film film) {
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
    public Optional<Film> updateFilm(Film film) {
        String sql = "update films set name = ?, description = ?, release_Date = ?, duration = ?, mpa_Id = ?" +
                " where id = ?";
        jdbcTemplate.update(sql
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , film.getMpa());
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
}