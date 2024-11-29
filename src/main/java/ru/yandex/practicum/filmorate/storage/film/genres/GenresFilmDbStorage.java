package ru.yandex.practicum.filmorate.storage.film.genres;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.GenresFilm;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j // Аннотация для логирования
@Repository // Аннотация, указывающая, что этот класс является репозиторием Spring
public class GenresFilmDbStorage extends BaseStorage<GenresFilm> {
    // SQL-запросы для работы с таблицей GENRES_FILM
    private static final String INSERT_QUERY = "INSERT INTO GENRES_FILM(film_id, genre_id) VALUES (?, ?)";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM GENRES_FILM gf JOIN GENRE_TYPE gt " +
            "ON gf.GENRE_ID = gt.GENRE_ID WHERE film_id = ?";
    private static final String DELETE_GENRES_BY_FILM_ID = "DELETE FROM GENRES_FILM WHERE FILM_ID = ?";

    public GenresFilmDbStorage(JdbcTemplate jdbc, RowMapper<GenresFilm> mapper) {
        super(jdbc, mapper, GenresFilm.class);
    }

    // Получение списка жанров по идентификатору фильма
    public List<GenresFilm> getGenresByFilmId(Long id) {
        log.info("Запрос на получение жанров для фильма с id: {}", id); // Логируем запрос на получение жанров
        List<GenresFilm> genres = findMany(FIND_BY_FILM_ID_QUERY, id);
        log.info("Найдено {} жанров для фильма с id: {}", genres.size(), id); // Логируем количество найденных жанров
        return genres; // Возвращаем список жанров
    }

    // Добавление жанра к фильму
    public void addGenreToFilm(Long filmId, Long genreId) {
        log.info("Добавление жанра с id: {} к фильму с id: {}", genreId, filmId); // Логируем добавление жанра к фильму
        if (insert(INSERT_QUERY, filmId, genreId) > 0) {
            log.info("Жанр с id: {} успешно добавлен к фильму с id: {}", genreId, filmId); // Логируем успешное добавление
        } else {
            throw new InternalServerException("Не удалось добавить жанр с id: " + genreId +
                    " к фильму с id: " + filmId);
        }
    }

    public boolean deleteGenresByFilmId(Long filmId) {
        log.info("Удаляем жанры фильма с id: {}", filmId);
        boolean result = delete(DELETE_GENRES_BY_FILM_ID, filmId);
        if (result) {
            log.info("Жанры успешно удалены по id фильма = {}", filmId); // Логируем успешное удаление
        }
        return result;
    }
    }