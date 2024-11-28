package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RestController // Указывает, что этот класс является REST-контроллером
@RequestMapping("/films") // Устанавливает базовый путь для всех методов контроллера
public class FilmController {

    // Константа для хранения значения по умолчанию количества популярных фильмов, отображаемых в ответе
    private static final String DEFAULT_COUNT_POPULAR_MOVIES_DISPLAYED = "10";

    private final FilmService filmService; // Сервис для работы с фильмами

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping // Обрабатывает GET-запросы по пути "/films"
    public Collection<FilmDto> findAll() {
        return filmService.getFilms(); // Возвращает все фильмы
    }

    @PostMapping // Обрабатывает POST-запросы по пути "/films"
    public FilmDto create(@Valid @RequestBody Film film) {
        return filmService.createFilm(film); // Создает новый фильм и возвращает его
    }

    @PutMapping // Обрабатывает PUT-запросы по пути "/films"
    public FilmDto update(@RequestBody UpdateFilmRequest request) {
        return filmService.updateFilm(request); // Обновляет фильм и возвращает обновленный объект
    }

    // Обрабатывает PUT-запросы по пути "/films/{id}/like/{userId}" для добавления лайка фильму
    @PutMapping("/{id}/like/{userId}")
    public FilmDto addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId); // Добавляет лайк фильму от пользователя и возвращает обновленный фильм
    }

    // Обрабатывает DELETE-запросы по пути "/films/{id}/like/{userId}" для удаления лайка у фильма
    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        // Удаляет лайк у фильма от пользователя и возвращает обновленный фильм
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getMostPopularByNumberOfLikes(
            @RequestParam(required = false, defaultValue = DEFAULT_COUNT_POPULAR_MOVIES_DISPLAYED) Long count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year) {
        // Возвращает список популярных фильмов в зависимости от заданного количества, жанра и года
        return filmService.getMostPopularByNumberOfLikes(count, genreId, year);
    }

    // Обрабатывает GET-запросы по пути "/films/{id}" для получения фильма с жанром по его ID
    @GetMapping("/{id}")
    public FilmDto getWithGenre(@PathVariable Long id) {
        return filmService.getWithGenre(id);
    }
}