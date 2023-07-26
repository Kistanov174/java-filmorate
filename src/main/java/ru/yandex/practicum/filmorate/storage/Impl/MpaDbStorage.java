package ru.yandex.practicum.filmorate.storage.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Validated
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_MPA_RATING_BY_ID = "select * from mpa_ratings where mpa_id = ?";
    private static final String SELECT_ALL_MPA_RATINGS = "select * from mpa_ratings order by mpa_id";

    public Mpa getMpaById(Integer id) {
        return jdbcTemplate.queryForObject(SELECT_MPA_RATING_BY_ID, (rs, rowNum) -> makeMpa(rs), id);
    }

    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(SELECT_ALL_MPA_RATINGS, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("mpa_id");
        String name = rs.getString("mpa_name");
        return new Mpa(id, name);
    }
}