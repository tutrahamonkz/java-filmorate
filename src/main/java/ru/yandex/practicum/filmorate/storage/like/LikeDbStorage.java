package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j // Аннотация для включения логирования в класс
@Repository // Аннотация, указывающая, что класс является репозиторием Spring
public class LikeDbStorage extends BaseStorage<Like> {
    // SQL-запросы для работы с таблицей LIKES
    private static final String INSERT_QUERY = "INSERT INTO LIKES(film_id, user_id) VALUES (?, ?)";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM LIKES WHERE film_id = ?";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM LIKES WHERE user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";

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
}