package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.List;
import java.util.Optional;

public interface MpaService {
    Optional<Mpa> findMpaById(Integer id);

    Optional<List<Mpa>> findAllMpa();
}
