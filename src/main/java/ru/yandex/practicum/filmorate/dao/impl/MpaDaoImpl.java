package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_MPA_RATING_BY_ID = "select * from mpa_ratings where mpa_id = ?";
    private static final String SELECT_ALL_MPA_RATINGS = "select * from mpa_ratings order by mpa_id";

    @Override
    public Optional<Mpa> getMpaById(Integer id) {
        return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(SELECT_MPA_RATING_BY_ID,
               (rs, rowNum) -> makeMpa(rs), id)).stream().findFirst())
                .orElseThrow(() -> new ObjectNotFoundException("Mpa with id = " + id + " doesn't exist." +
                MpaDaoImpl.class.getSimpleName()));
    }

    @Override
    public Optional<List<Mpa>> getAllMpa() {
        return Optional.of(jdbcTemplate.queryForStream(SELECT_ALL_MPA_RATINGS, (rs, rowNum) -> makeMpa(rs)).findFirst()
                .orElseGet(ArrayList::new));
    }

    private List<Mpa> makeMpa(ResultSet rs) throws SQLException {
        List<Mpa> mpas = new ArrayList<>();
        do {
            Integer id = rs.getInt("mpa_id");
            String name = rs.getString("mpa_name");
            mpas.add(new Mpa(id, name));
        } while (rs.next());
        return mpas;
    }
}