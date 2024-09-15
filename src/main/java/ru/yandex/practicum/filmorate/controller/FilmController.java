package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final static int MAXIMUM_DESCRIPTION_LENGTH = 200;
    private final static LocalDate MINIMUM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (validate(film)) {
            film.setId(getNextId());
            films.put(film.getId(), film);
            System.out.println(film.getDuration());
            log.info("Создан фильм с id: {}", film.getId());
            return film;
        }
        throw new ValidationException("Такой фильм не может быть создан");
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            if (validate(newFilm)) {
                films.put(newFilm.getId(), newFilm);
                log.info("Обновлен фильм с id: {}", newFilm.getId());
                return newFilm;
            }
        }
        throw new NotFoundException("Пользователь с id = " + newFilm.getId() + " не найден");
    }

    private boolean validate(Film film) {
        if (film.getReleaseDate().isBefore(MINIMUM_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не должна быть раньше " + MINIMUM_RELEASE_DATE);
        }
        if (film.getDescription().length() > MAXIMUM_DESCRIPTION_LENGTH) {
            throw new ValidationException("Длинна описания не должна превышать " +
                    MAXIMUM_DESCRIPTION_LENGTH + " символов");
        }
        if (film.getDuration().isNegative()) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        return true;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
