package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Objects;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_INTO_FILM_LIKES = "insert into film_likes(film_id, user_id) values(?, ?)";
    private static final String DELETE_INTO_FILM_LIKES = "delete from film_likes where film_id = ? and user_id = ?";
    private static final String SELECT_MOST_POPULAR_FILMS = "select f.id, f.name, f.description, f.duration, " +
            "f.release_Date, f.mpa_id, mr.mpa_name, fg.genre_id, g.genre_name, fl.user_id," +
            "count(fl.user_id) as rate " +
            "from films as f left join film_likes as fl on f.id = fl.film_id " +
            "left join film_genre as fg on f.id = fg.film_id " +
            "left join genres as g on fg.genre_id = g.genre_id " +
            "left join mpa_ratings as mr on f.mpa_id = mr.mpa_id " +
            "group by f.id, fl.user_id " +
            "order by rate desc " +
            "limit ?";
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

    @Override
    public Optional<List<Film>> getAllFilms() {
        return Optional.of(jdbcTemplate.queryForStream(SELECT_ALL_FILMS, (rs, rowNum) -> makeFilm(rs))
                .findFirst()
                .orElseGet(ArrayList::new));
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(SELECT_FILM_BY_ID,
                                (rs, rowNum) -> makeFilm(rs), id))
                .stream()
                        .findFirst())
                .orElseThrow(() -> new ObjectNotFoundException("Film with id = " + id + " doesn't exist." +
                        FilmDaoImpl.class.getSimpleName()));
    }

    @Override
    public Optional<Integer> createFilm(Film film) {
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
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        updateGenre(id, film.getGenres());
        return Optional.of(id);
    }

    @Override
    public void updateFilm(Film film) {
        jdbcTemplate.update(UPDATE_FILMS,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        updateGenre(film.getId(), film.getGenres());
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        jdbcTemplate.update(INSERT_INTO_FILM_LIKES, id, userId);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        jdbcTemplate.update(DELETE_INTO_FILM_LIKES, id, userId);
    }

    @Override
    public Optional<List<Film>> showMostPopularFilms(Integer limit) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_MOST_POPULAR_FILMS,
                (rs, rowNum) -> makeFilm(rs), limit));
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

    private void updateGenre(int filmId, Collection<Genre> genreId) {
        jdbcTemplate.update(DELETE_FROM_FILM_GENRE, filmId);
        List<Object[]> batch = new ArrayList<>();
        for (Genre genre : genreId) {
            Object[] values = new Object[] {
                    filmId, genre.getId()};
            batch.add(values);
        }
        this.jdbcTemplate.batchUpdate(INSERT_INTO_FILM_GENRE, batch);
    }
}