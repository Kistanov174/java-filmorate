package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserDao userDao;
	private final UserService userService;
	private final FilmService filmService;
	private final FilmDao filmDao;

	@Test
	public void testCreateAndUpdateUser() {
		Optional<User> newUser = userService.createUser(new User(null, "email@gmail.com", "login",
				"user", LocalDate.of(2000, 1, 1)));
		assertThat(newUser).isPresent()
				.hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("name", "user"));
		updateUser();
	}

	@Test
	public void testGetUserById() {
		Optional<User> userOptional = userDao.getUserById(1);
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testGetAllUsers() {
		Optional<List<User>> newUsers = userDao.getAllUsers();
		assertThat(newUsers)
				.isPresent()
				.hasValueSatisfying(users ->
						assertThat(users).isNotNull()
				);
	}

	private void updateUser() {
		Optional<User> user = userDao.getUserById(1);
		user.ifPresent(value -> value.setName("newName"));
		userDao.updateUser(user.get());
		Optional<User> userOptional = userDao.getUserById(user.get().getId());
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(updatedUser ->
						assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "newName")
				);
	}

	@Test
	public void testCreateAndUpdateFilm() {
		Optional<Film> filmOptional = filmService.createFilm(new Film(null, "newFilm",
				"description film", LocalDate.of(2023, 5, 1), 120, 0,
				new Mpa(1, "")));
		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", 2));
		updateFilm();
	}

	private void updateFilm() {
		Optional<Film> filmOptional = filmDao.getFilmById(1);
		filmOptional.ifPresent(film -> film.setDuration(100));
		filmDao.updateFilm(filmOptional.get());
		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("duration", 100)
				);
	}

	@Test
	public void testGetFilmById() {
		Optional<Film> newFilm = filmService.createFilm(new Film(null, "newFilm",
				"description film", LocalDate.of(2023, 5, 1), 120, 0,
				new Mpa(1, "")));
		Optional<Film> filmOptional = filmDao.getFilmById(1);
		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("name", "newFilm"));
	}

	@Test
	public void testGetAllFilms() {
		Optional<List<Film>> newFilms = filmDao.getAllFilms();
		assertThat(newFilms)
				.isPresent()
				.hasValueSatisfying(films ->
						assertThat(films).isNotNull()
				);
	}
}