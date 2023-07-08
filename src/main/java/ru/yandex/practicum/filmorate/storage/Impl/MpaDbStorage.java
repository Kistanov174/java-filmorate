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

    public Mpa getMpaById(Integer id) {
        String sql = "select * from mpa_ratings where mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), id);
    }

    public List<Mpa> getAllMpa() {
        String sql = "select * from mpa_ratings order by mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("mpa_id");
        String name = rs.getString("mpa_name");
        return new Mpa(id, name);
    }
}