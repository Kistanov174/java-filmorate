package ru.yandex.practicum.filmorate.storage.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.GenreController;
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
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Genre> getGenreById(Integer id) {
        String sql = "select * from genres where genre_id = ?";
        return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), id))
                        .stream()
                        .findFirst())
                .orElseThrow(() -> new ObjectNotFoundException("Genre with id = " + id + " doesn't exist." +
                        GenreController.class.getSimpleName()));
    }

    public Optional<List<Genre>> getAllGenres() {
        String sql = "select * from genres order by genre_id";
        return Optional.of(jdbcTemplate.queryForStream(sql, (rs, rowNum) -> makeGenre(rs)).findFirst()
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