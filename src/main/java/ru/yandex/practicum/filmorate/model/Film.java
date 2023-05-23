package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMin;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Min(0)
    private Integer duration;
}



