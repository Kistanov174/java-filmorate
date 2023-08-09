package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaDao mpaDao;

    @Override
    public Optional<Mpa> findMpaById(Integer id) {
        return mpaDao.getMpaById(id);
    }

    @Override
    public Optional<List<Mpa>> findAllMpa() {
        return mpaDao.getAllMpa();
    }
}
