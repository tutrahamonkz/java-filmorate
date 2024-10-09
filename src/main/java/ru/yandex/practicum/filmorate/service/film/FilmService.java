package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service // Аннотация указывает, что данный класс является сервисом и может быть использован в контексте Spring
public class FilmService {

    private final FilmStorage filmStorage; // Хранение ссылки на объект FilmStorage для работы с данными о фильмах
    private final UserStorage userStorage; // Хранилище пользователей для проверки существования пользователей

    // Конструктор, принимающий FilmStorage в качестве параметра
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // Метод для получения всех фильмов из хранилища
    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    // Метод для получения фильма по его идентификатору
    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    // Метод для создания нового фильма
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    // Метод для обновления существующего фильма
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    // Метод для добавления лайка к фильму от пользователя
    public Film addLike(Long filmId, Long userId) {
        userStorage.checkContainsUserId(userId); // Проверяем, существует ли пользователь с данным идентификатором
        return filmStorage.addLike(filmId, userId);
    }

    // Метод для удаления лайка от пользователя к фильму
    public Film deleteLike(Long filmId, Long userId) {
        userStorage.checkContainsUserId(userId); // Проверяем, существует ли пользователь с данным идентификатором
        return filmStorage.deleteLike(filmId, userId);
    }

    // Метод для получения самых популярных фильмов по количеству лайков
    public Collection<Film> getMostPopularByNumberOfLikes(Long count) {
        return filmStorage.getMostPopularByNumberOfLikes(count);
    }
}