package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

// Интерфейс FilmStorage определяет методы для работы с фильмами
public interface FilmStorage {

    // Метод для получения всех фильмов из хранилища
    Collection<Film> getFilms();

    // Метод для получения фильма по его идентификатору
    Film getFilmById(Long filmId);

    // Метод для создания нового фильма
    Film createFilm(Film film);

    // Метод для обновления существующего фильма
    Film updateFilm(Film film);

    // Метод для добавления лайка к фильму от пользователя
    Film addLike(Long filmId, Long userId, UserStorage userStorage);

    // Метод для удаления лайка от пользователя к фильму
    Film deleteLike(Long filmId, Long userId, UserStorage userStorage);

    // Метод для получения самых популярных фильмов по количеству лайков
    Collection<Film> getMostPopularByNumberOfLikes(Long count);
}