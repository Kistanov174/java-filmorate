package ru.yandex.practicum.filmorate.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("filmDbService")
@RequiredArgsConstructor
public class FilmDbService implements FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmDbStorage;

    @Override
    public Optional<Film> addLike(Integer id, Integer userId) {
        String sql = "insert into film_likes(film_id, user_id) values(?, ?)";
        jdbcTemplate.update(sql, id, userId);
        return filmDbStorage.getFilmById(id);
    }

    @Override
    public Optional<Film> deleteLike(Integer id, Integer userId) {
        String sql = "delete from film_likes(film_id, user_id) values(?, ?)";
        jdbcTemplate.update(sql, id, userId);
        return filmDbStorage.getFilmById(id);
    }

    public Optional<Film> addGenre(Integer id, Integer genre_id) {
        String sql = "insert into film_genre(film_id, genre_id) values(?, ?)";
        jdbcTemplate.update(sql, id, genre_id);
        return filmDbStorage.getFilmById(id);
    }

    public Optional<Film> deleteGenre(Integer id, Integer genre_id) {
        String sql = "delete from film_genre(film_id, genre_id) values(?, ?)";
        jdbcTemplate.update(sql, id, genre_id);
        return filmDbStorage.getFilmById(id);
    }

    @Override
    public List<Film> showMostPopularFilms(Integer count) {
        String sql = "select * from films order by rate desc limit count";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs));
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
        } while(!rs.isAfterLast());
        return films;
    }
}