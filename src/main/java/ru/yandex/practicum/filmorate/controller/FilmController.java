package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j // Аннотация для автоматической генерации логгера
@RestController // Указывает, что этот класс является REST-контроллером
@RequestMapping("/films") // Устанавливает базовый путь для всех методов контроллера
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>(); // Хранение фильмов в виде пары "идентификатор - фильм"

    @GetMapping // Обрабатывает GET-запросы по пути "/films"
    public Collection<Film> findAll() {
        return films.values(); // Возвращает все фильмы
    }

    @PostMapping // Обрабатывает POST-запросы по пути "/films"
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId()); // Генерируем следующий идентификатор для нового фильма
        films.put(film.getId(), film); // Сохраняем фильм в коллекцию
        log.info("Создан фильм с id: {}", film.getId()); // Логируем информацию о создании фильма
        return film; // Возвращаем созданный фильм
    }

    @PutMapping // Обрабатывает PUT-запросы по пути "/films"
    public Film update(@Valid @RequestBody Film newFilm) {
        // Проверяем, существует ли фильм с указанным идентификатором
        if (films.containsKey(newFilm.getId())) {
            films.put(newFilm.getId(), newFilm); // Обновляем информацию о фильме
            log.info("Обновлен фильм с id: {}", newFilm.getId()); // Логируем информацию об обновлении фильма
            return newFilm; // Возвращаем обновленный фильм
        }
        // Если фильм не найден, выбрасываем исключение NotFoundException
        throw new NotFoundException("Фильм с id: " + newFilm.getId() + " не найден");
    }

    // Метод для генерации следующего идентификатора
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
