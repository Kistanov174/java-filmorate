package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaDao mpaDao;

    @Override
    public Mpa findMpaById(Integer id) {
        return mpaDao.getMpaById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Mpa with id = " + id + " hasn't found"));
    }

    @Override
    public List<Mpa> findAllMpa() {
        return mpaDao.getAllMpa()
                .orElseThrow(() -> new ObjectNotFoundException("List of Mpa haven't found"));
    }
}
