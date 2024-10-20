package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@RestController // Аннотация, указывающая, что класс является REST-контроллером
@RequestMapping("/genres") // Общий путь для всех методов контроллера
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    // Метод для получения списка всех жанров
    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.getAllGenre();
    }

    // Метод для получения жанра по его идентификатору
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Long id) {
        return genreService.getGenreById(id);
    }
}
