package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

// Интерфейс FilmStorage определяет методы для работы с фильмами
public interface FilmStorage {

    // Метод для получения всех фильмов из хранилища
    List<Film> getFilms();

    // Метод для получения фильма по его идентификатору
    Optional<Film> getFilmById(Long filmId);

    // Метод для создания нового фильма
    Film createFilm(Film film);

    // Метод для обновления существующего фильма
    Film updateFilm(Film film);

    //Вывод самых популярных фильмов по жанру и годам.
    List<Film> getMostPopularByNumberOfLikes(Long count, Long genreId, Integer year);
}