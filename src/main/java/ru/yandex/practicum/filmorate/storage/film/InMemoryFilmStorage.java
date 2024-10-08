package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j // Аннотация для автоматической генерации логгера
@Component // Аннотация, указывающая, что данный класс является компонентом Spring
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>(); // Хранение фильмов в виде пары "идентификатор - фильм"

    @Override // Метод для получения всех фильмов из хранилища
    public Collection<Film> getFilms() {
        return films.values(); // Возвращаем коллекцию всех фильмов
    }

    @Override // Метод для получения фильма по его идентификатору
    public Film getFilmById(Long filmId) {
        // Проверяем, существует ли фильм с данным идентификатором и возвращаем его
        return checkContainsFilmId(filmId);
    }

    @Override // Метод для создания нового фильма
    public Film createFilm(Film film) {
        film.setId(getNextId()); // Генерируем следующий идентификатор для нового фильма
        films.put(film.getId(), film); // Сохраняем фильм в коллекцию
        log.info("Создан фильм с id: {}", film.getId()); // Логируем информацию о создании фильма
        return film; // Возвращаем созданный фильм
    }

    @Override  // Метод для обновления существующего фильма
    public Film updateFilm(Film film) {
        checkContainsFilmId(film.getId());
        films.put(film.getId(), film); // Обновляем информацию о фильме
        log.info("Обновлен фильм с id: {}", film.getId()); // Логируем информацию об обновлении фильма
        return film; // Возвращаем обновленный фильм
    }

    @Override  // Метод для добавления лайка к фильму от пользователя
    public Film addLike(Long filmId, Long userId, UserStorage userStorage) {
        Film film = checkContainsFilmId(filmId); // Проверяем, существует ли фильм с данным идентификатором
        userStorage.checkContainsUserId(userId); // Проверяем, существует ли пользователь с данным идентификатором
        film.getLikes().add(userId); // Добавляем идентификатор пользователя в список лайков фильма
        // Логируем действие пользователя
        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
        return film; // Возвращаем обновленный фильм
    }

    @Override // Метод для удаления лайка от пользователя к фильму
    public Film deleteLike(Long filmId, Long userId, UserStorage userStorage) {
        Film film = checkContainsFilmId(filmId); // Проверяем, существует ли фильм с данным идентификатором
        userStorage.checkContainsUserId(userId); // Проверяем, существует ли пользователь с данным идентификатором
        film.getLikes().remove(userId); // Удаляем идентификатор пользователя из списка лайков фильма
        // Логируем действие пользователя
        log.info("Пользователь с id: {} удалил лайк фильму с id: {}", userId, filmId);
        return film; // Возвращаем обновленный фильм
    }

    @Override // Метод для получения самых популярных фильмов по количеству лайков
    public List<Film> getMostPopularByNumberOfLikes(Long count) {
        // Логируем запрос на получение популярных фильмов
        log.info("Пользователь получил список популярных фильмов");
        return films.values().stream()
                // Сортируем фильмы по количеству лайков (по убыванию)
                .sorted(((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size())))
                .limit(count) // Ограничиваем количество возвращаемых фильмов до заданного значения
                .toList(); // Собираем результат в список и возвращаем его
    }

    // Приватный метод для генерации следующего идентификатора фильма
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // Приватный метод для проверки существования фильма по его идентификатору
    private Film checkContainsFilmId(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм c id: " + filmId + " не найден");
        }
        return films.get(filmId);
    }
}