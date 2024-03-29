package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.Birthday;
import ru.yandex.practicum.filmorate.annotation.Login;
import ru.yandex.practicum.filmorate.validation.Marker;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    public final Set<Integer> friends = new HashSet<>();
}