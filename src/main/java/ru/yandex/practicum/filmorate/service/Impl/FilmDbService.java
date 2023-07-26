package ru.yandex.practicum.filmorate.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.List;

@Service("filmDbService")
@RequiredArgsConstructor
public class FilmDbService implements FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmDbStorage;
    private final UserDbService userDbService;
    private final FilmMaker filmMaker;
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

    @Override
    public void addLike(Integer id, Integer userId) {
        userDbService.checkUser(userId);
        jdbcTemplate.update(INSERT_INTO_FILM_LIKES, id, userId);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        userDbService.checkUser(userId);
        jdbcTemplate.update(DELETE_INTO_FILM_LIKES, id, userId);
    }

    @Override
    public List<Film> showMostPopularFilms(Integer limit) {
        return jdbcTemplate.queryForObject(SELECT_MOST_POPULAR_FILMS, (rs, rowNum) -> filmMaker.makeFilm(rs), limit);
    }
}