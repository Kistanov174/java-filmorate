package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Mpa {
    public Mpa() {

    }

    @NotNull
    private Integer id;
    @NotNull
    private String name;
}