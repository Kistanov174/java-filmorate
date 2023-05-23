package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Birthday;
import ru.yandex.practicum.filmorate.annotation.Login;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Integer id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Login
    private String login;
    private String name;
    @Birthday
    private LocalDate birthday;

    public interface Marker {

        interface OnCreate {}

        interface OnUpdate {}
    }
}
