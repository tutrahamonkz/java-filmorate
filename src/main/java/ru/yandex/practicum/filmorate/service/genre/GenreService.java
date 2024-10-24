package ru.yandex.practicum.filmorate.service.genre;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

@Service // Аннотация указывает, что данный класс является сервисом и может быть использован в контексте Spring
public class GenreService {

    private final GenreDbStorage genreDbStorage;  // Хранилище для работы с данными жанров

    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    // Метод для получения списка всех жанров
    public List<Genre> getAllGenre() {
        return genreDbStorage.getAllGenres(); // Возвращаем все жанры из хранилища
    }

    // Метод для получения жанра по его идентификатору
    public Genre getGenreById(Long id) {
        // Пытаемся получить жанр по ID. Если не найден, выбрасываем исключение NotFoundException.
        return genreDbStorage.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id: " + id + " не найден"));
    }
}