package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_GENRE_BY_ID = "select * from genres where genre_id = ?";
    private static final String SELECT_ALL_GENRES = "select * from genres order by genre_id";

    @Override
    public Optional<Genre> getGenreById(final Integer id) {
        return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(SELECT_GENRE_BY_ID,
                                (rs, rowNum) -> makeGenre(rs), id)).stream().findFirst())
                .orElseThrow(() -> new ObjectNotFoundException("Genre with id = " + id + " doesn't exist." +
                        GenreDaoImpl.class.getSimpleName()));
    }

    @Override
    public Optional<List<Genre>> getAllGenres() {
        return Optional.of(jdbcTemplate.queryForStream(SELECT_ALL_GENRES, (rs, rowNum) -> makeGenre(rs)).findFirst()
                .orElseGet(ArrayList::new));
    }

    private List<Genre> makeGenre(ResultSet rs) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        do {
            Integer id = rs.getInt("genre_id");
            String name = rs.getString("genre_name");
            genres.add(new Genre(id, name));
        } while (rs.next());
        return genres;
    }
}