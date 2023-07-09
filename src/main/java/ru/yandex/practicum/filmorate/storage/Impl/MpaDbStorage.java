package ru.yandex.practicum.filmorate.storage.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Validated
@RequiredArgsConstructor
public class MpaDbStorage {
    JdbcTemplate jdbcTemplate;

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Integer id) {
        String sql = "select * from genres where id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), id);
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        String sql = "select * from genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}