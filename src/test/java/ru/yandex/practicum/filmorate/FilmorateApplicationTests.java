package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {
	private Film film;
	private User user;
	private final UserController userController;
	private final FilmController filmController;

	@Autowired
	public FilmorateApplicationTests(UserController userController, FilmController filmController) {
		this.userController = userController;
		this.filmController = filmController;
	}

	@BeforeEach
	public void beforeEach() {
		film = new Film(null, "Grimm", "Fantasy", LocalDate.of(2022, 9, 7),
				0, 0);
		user = new User(null,"aleksey.kistanov@yandex.ru", "leksa", "Миша", null);
	}

	@Test
	@DisplayName("Проверка валидации email-адреса у объекта класса User")
	void shouldGetValidationEmailExceptionWhenEmailHasWrongEmailAddress() {
		user.setEmail("aleksey.kistanovyandex.ru");
		ValidationException ex1 = assertThrows(ValidationException.class, () -> userController.createUser(user));
		assertEquals("Неправильный формат email в запросе UserController", ex1.getMessage());
		user.setEmail("");
		ValidationException ex2 = assertThrows(ValidationException.class, () -> userController.createUser(user));
		assertEquals("Пустой email-адрес в запросе UserController", ex2.getMessage());
	}

	@Test
	@DisplayName("Проверка валидации login у объекта класса User")
	void shouldGetValidationLoginExceptionWhenLoginIsBlank() {
		user.setLogin("");
		ValidationException ex1 = assertThrows(ValidationException.class, () -> userController.createUser(user));
		assertEquals("Пустой login в запросе UserController", ex1.getMessage());
		user.setLogin("  ");
		ValidationException ex2 = assertThrows(ValidationException.class, () -> userController.createUser(user));
		assertEquals("Пустой login в запросе UserController", ex2.getMessage());
	}

	@Test
	@DisplayName("Проверка замены пустого name на login у объекта класса User")
	void shouldGetLoginInsteadOfEmptyName() {
		userController.createUser(user);
		user.setName("");
		User updateUser = userController.updateUser(user);
		assertEquals("leksa", updateUser.getName());
	}

	@Test
	@DisplayName("Проверка валидации birthday у объекта класса User")
	void shouldGetValidationBirthdayExceptionWhenBirthdayIsInFuture() {
		user.setBirthday(LocalDate.of(2200, 1, 1));
		ValidationException ex = assertThrows(ValidationException.class, () -> userController.createUser(user));
		assertEquals("Дата рождения еще не наступила в запросе UserController", ex.getMessage());
		user.setBirthday(LocalDate.now());
		userController.createUser(user);
		assertEquals(LocalDate.now(), user.getBirthday(), "User с текущей датой рождения не создался");
	}

	@Test
	@DisplayName("Проверка валидации поля name у объекта класса Film")
	void shouldGetValidationNameFilmExceptionWhenNameFilmIsBlank() {
		film.setName("");
		ValidationException ex1 = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
		assertEquals("Пустое имя фильма в запросе FilmController", ex1.getMessage());
		film.setName(" ");
		ValidationException ex2 = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
		assertEquals("Пустое имя фильма в запросе FilmController", ex2.getMessage());
	}

	@Test
	@DisplayName("Проверка валидации поля description у объекта класса Film")
	void shouldGetValidationDescriptionFilmExceptionWhenDescriptionIsTooLong() {
		film.setDescription("Действие происходит в современном Портленде, где детектив из отдела убийств узнаёт, " +
				"что он является потомком группы охотников, известных как «Гриммы», которые сражаются," +
				" чтобы сохранить человечество в безопасности от сверхъестественных существ. Узнав о своей судьбе," +
				" и том, что он является последним из своего рода, он должен защитить каждую живую душу от зловещих" +
				" персонажей сборника сказок, которые проникли в реальный мир.");
		ValidationException ex = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
		assertEquals("Слишком длинное описание фильма в запросе FilmController", ex.getMessage());
	}

	@Test
	@DisplayName("Проверка валидации поля releaseDate у объекта класса Film")
	void shouldGetValidationReleaseDateFilmExceptionWhenReleaseDateIsTooOld() {
		film.setReleaseDate(LocalDate.of(1894, 12, 28));
		ValidationException ex = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
		assertEquals("Слишком старая дата релиза в запросе FilmController", ex.getMessage());
		film.setReleaseDate(LocalDate.of(1895, 12, 28));
		Film newFilm = filmController.createFilm(film);
		assertEquals(newFilm.getReleaseDate(), LocalDate.of(1895, 12, 28));
	}

	@Test
	@DisplayName("Проверка валидации поля duration у объекта класса Film")
	void shouldGetValidationDurationFilmExceptionWhenDurationIsNegative() {
		film.setDuration(-57);
		ValidationException ex = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
		assertEquals("Отрицательная продолжительность в запросе FilmController", ex.getMessage());
		film.setDuration(0);
		Film newFilm = filmController.createFilm(film);
		assertEquals(newFilm.getDuration(), 0);
		film.setDuration(59);
		newFilm = filmController.updateFilm(film);
		assertEquals(newFilm.getDuration(), 59);
	}
}