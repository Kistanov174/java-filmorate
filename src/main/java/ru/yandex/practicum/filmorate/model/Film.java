package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;
import ru.yandex.practicum.filmorate.validation.Marker;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    public Film() {

    }

    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Min(0)
    private Integer duration;
    private Integer rate;
    private Mpa mpa;
    private final Set<Genre> genres = new HashSet<>();
    public final Set<Integer> likes = new HashSet<>();
}