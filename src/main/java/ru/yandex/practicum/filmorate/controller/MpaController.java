package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Optional<List<Mpa>> getAllMpa() {
        log.info("Request to get all mpa");
        return mpaService.findAllMpa();
    }

    @GetMapping("/{id}")
    public Optional<Mpa> getMpaById(@PathVariable Integer id) {
        log.info("Request to get mpa by id");
        return mpaService.findMpaById(id);
    }
}