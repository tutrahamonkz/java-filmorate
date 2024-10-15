package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public class FilmDbStorage implements FilmStorage {
    @Override
    public Collection<Film> getFilms() {
        return List.of();
    }

    @Override
    public Film getFilmById(Long filmId) {
        return null;
    }

    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        return null;
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        return null;
    }

    @Override
    public Collection<Film> getMostPopularByNumberOfLikes(Long count) {
        return List.of();
    }
}
