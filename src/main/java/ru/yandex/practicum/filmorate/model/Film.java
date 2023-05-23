package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.convert.DurationFormat;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.boot.convert.DurationUnit;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
    @DurationUnit(ChronoUnit.MINUTES)
    @DurationFormat(DurationStyle.SIMPLE)
    @DurationMin(minutes = 0)
    private Duration duration = Duration.ofMinutes(0);
    private int rate;
}



