package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Slf4j // Аннотация для включения логирования в класс
@Repository // Аннотация, указывающая, что класс является репозиторием Spring
public class LikeDbStorage extends BaseStorage<Like> {
    // SQL-запросы для работы с таблицей LIKES
    private static final String INSERT_QUERY = "INSERT INTO LIKES(film_id, user_id) VALUES (?, ?)";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM LIKES WHERE film_id = ?";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM LIKES WHERE user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";
    private static final String DELETE_QUERY_BY_FILM_ID = "DELETE FROM LIKES WHERE film_id = ?";
    private static final String DELETE_QUERY_BY_USER_ID = "DELETE FROM LIKES WHERE user_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM LIKES";
    private static final String SEARCH_POST_LIKE = "SELECT * FROM LIKES WHERE film_id =? AND user_id = ?";

    public LikeDbStorage(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper, Like.class);
    }

    // Метод для получения списка лайков по ID фильма
    public List<Like> getLikesByFilmId(Long id) {
        log.info("Получение лайков для фильма с ID: {}", id); // Логируем получение лайков
        List<Like> likes = findMany(FIND_BY_FILM_ID_QUERY, id); // Выполняем запрос к БД
        log.info("Найдено {} лайков для фильма с ID: {}", likes.size(), id); // Логируем количество найденных лайков
        return likes; // Возвращаем список лайков
    }

    // Метод для получения списка лайков по ID пользователя
    public List<Like> getLikesByUserId(Long id) {
        log.info("Получение лайков для пользователя с ID: {}", id); // Логируем получение лайков
        List<Like> likes = findMany(FIND_BY_USER_ID_QUERY, id); // Выполняем запрос к БД
        // Логируем количество найденных лайков
        log.info("Найдено {} лайков для пользователя с ID: {}", likes.size(), id);
        return likes; // Возвращаем список лайков
    }

    // Метод для получения всех лайков
    public List<Like> getAllLikes() {
        log.info("Получения списка всех лайков");
        List<Like> likes = findMany(FIND_ALL_QUERY); // Выполняем запрос к БД
        log.info("Найдено {} лайков", likes.size());
        return likes;
    }

    // Метод для добавления лайка к фильму
    public Like addLikeToFilm(Long filmId, Long userId) {
        // Логируем добавление лайка
        log.info("Добавление лайка для фильма с ID: {} от пользователя с ID: {}", filmId, userId);
        long id = insert(INSERT_QUERY, filmId, userId); // Выполняем вставку в БД
        if (id > 0) { // Проверяем успешность добавления
            // Логируем успешное добавление
            log.info("Лайк успешно добавлен для фильма с ID: {} от пользователя с ID: {}", filmId, userId);
            return Like.builder() // Возвращаем новый объект Like
                    .filmId(filmId)
                    .userId(userId)
                    .build();
        }
        throw new InternalServerException("Лайк не добавлен"); // Исключение в случае ошибки добавления
    }

    // Метод для удаления лайка от фильма
    public void deleteLike(Long filmId, Long userId) {
        // Логируем удаление лайка
        log.info("Удаление лайка для фильма с ID: {} от пользователя с ID: {}", filmId, userId);
        if (delete(DELETE_QUERY, filmId, userId)) { // Выполняем удаление из БД
            // Логируем успешное удаление
            log.info("Лайк успешно удален для фильма с ID: {} от пользователя с ID: {}", filmId, userId);
        } else {
            throw new InternalServerException("Не удалось удалить лайк для фильма с ID: " + filmId +
                    " от пользователя с ID: " + userId);
        }
    }

    public void deleteLikeByFilmId(Long filmId) {
        // Логируем удаление лайка
        log.info("Удаление лайков для фильма с ID: {}", filmId);
        if (delete(DELETE_QUERY_BY_FILM_ID, filmId)) { // Выполняем удаление из БД
            // Логируем успешное удаление
            log.info("Лайки успешно удалены для фильма с ID: {}", filmId);
        } else {
            throw new InternalServerException("Не удалось удалить лайки для фильма с ID: ");
        }
    }

    public void deleteLikeByUserId(Long userId) {
        // Логируем удаление лайка
        log.info("Удаление лайков для пользователя с ID: {}", userId);
        if (delete(DELETE_QUERY_BY_USER_ID, userId)) { // Выполняем удаление из БД
            // Логируем успешное удаление
            log.info("Лайки успешно удалены для пользователя с ID: {}", userId);
        } else {
            throw new InternalServerException("Не удалось удалить лайки для пользователя с ID: ");
        }
    }
    //метод для проверки наличия лайка в таблице

    public Optional<Like> searchLikeByUserIdFilmId(Long filmId, Long userId) {
        return findOne(SEARCH_POST_LIKE, filmId, userId);
    }
}