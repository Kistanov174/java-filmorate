package ru.yandex.practicum.filmorate.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.List;
import java.util.Optional;

@Service("filmDbService")
@RequiredArgsConstructor
public class FilmDbService implements FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmDbStorage;
    private final UserDbService userDbService;
    private final FilmMaker filmMaker;

    @Override
    public Optional<Film> addLike(Integer id, Integer userId) {
        String sql = "insert into film_likes(film_id, user_id) values(?, ?)";
        userDbService.checkUser(userId);
        jdbcTemplate.update(sql, id, userId);
        return filmDbStorage.getFilmById(id);
    }

    @Override
    public Optional<Film> deleteLike(Integer id, Integer userId) {
        String sql = "delete from film_likes where film_id = ? and user_id = ?";
        userDbService.checkUser(userId);
        jdbcTemplate.update(sql, id, userId);
        return filmDbStorage.getFilmById(id);
    }

    @Override
    public List<Film> showMostPopularFilms(Integer limit) {
        String sql = "select f.id, f.name, f.description, f.duration, f.release_Date, f.mpa_id, mr.mpa_name, " +
                "fg.genre_id, g.genre_name, fl.user_id," +
                "count(fl.user_id) as rate " +
                "from films as f left join film_likes as fl on f.id = fl.film_id " +
                "left join film_genre as fg on f.id = fg.film_id " +
                "left join genres as g on fg.genre_id = g.genre_id " +
                "left join mpa_ratings as mr on f.mpa_id = mr.mpa_id " +
                "group by f.id, fl.user_id " +
                "order by rate desc " +
                "limit " + limit;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> filmMaker.makeFilm(rs));
    }
}