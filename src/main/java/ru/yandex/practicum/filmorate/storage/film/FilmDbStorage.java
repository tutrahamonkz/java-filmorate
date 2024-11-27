package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j // Аннотация для логирования
@Repository // Аннотация, указывающая, что этот класс является репозиторием Spring
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    // SQL-запросы для работы с таблицей FILMS
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILMS f JOIN MPA_TYPE mp ON f.mpa = mp.MPA_ID";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM FILMS f JOIN MPA_TYPE mp ON f.mpa = mp.MPA_ID " +
            "WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILMS(film_name, description, release_date, duration, " +
            "mpa) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILMS SET film_name = ?, description = ?, release_date = ?, " +
            "duration = ? WHERE film_id = ?";
    private static final String FIND_POPULAR_LIMIT_QUERY = "SELECT f.*, mp.MPA_NAME FROM FILMS f " +
            "LEFT JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
            "JOIN MPA_TYPE mp ON f.mpa = mp.MPA_ID " +
            "GROUP BY f.FILM_ID " +
            "ORDER BY COUNT(l.USER_ID) DESC " +
            "LIMIT ?;";
    private static final String SEARCH_FILM = """
            SELECT
                film.*,
                m_type.mpa_name,
                COUNT(DISTINCT f_like.user_id) as likes_count
            FROM films AS film
            LEFT JOIN mpa_type AS m_type ON film.mpa = m_type.mpa_id
            LEFT JOIN likes AS f_like ON film.film_id = f_like.film_id
            %s
            GROUP BY film.film_id
            ORDER BY likes_count DESC;
            """;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    // Получение списка всех фильмов
    @Override
    public List<Film> getFilms() {
        log.info("Запрос на получение всех фильмов.");
        return findMany(FIND_ALL_QUERY);
    }

    // Получение фильма по его идентификатору
    @Override
    public Optional<Film> getFilmById(Long filmId) {
        log.info("Запрос на получение фильма с id: {}", filmId);
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    // Создание нового фильма в базе данных
    @Override
    public Film createFilm(Film film) {
        Long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id); // Установка ID созданного фильма
        log.info("Создан фильм с id: {}", id); // Логируем создание фильма
        return film; // Возвращаем созданный фильм
    }

    // Обновление информации о фильме
    @Override
    public Film updateFilm(Film film) {
        log.info("Обновление фильма с id: {}", film.getId()); // Логируем начало обновления
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getId()
                );
        log.info("Фильм с id: {} успешно обновлён.", film.getId()); // Логируем успешное обновление
        return film; // Возвращаем обновленный фильм
    }

    // Получение самых популярных фильмов по количеству лайков с ограничением на количество
    @Override
    public List<Film> getMostPopularByNumberOfLikes(Long count) {
        // Логируем запрос на получение популярных фильмов
        log.info("Запрос на получение {} самых популярных фильмов.", count);
        return findMany(FIND_POPULAR_LIMIT_QUERY, count);
    }

    @Override
    public List<Film> search(String queryStr, List<String> by) {
        String filter = "";
        if (by.contains("title")) {
            filter = "WHERE LOWER(film.film_name) LIKE LOWER(?)";
        } else {
            // todo director filter
            // todo exception
        }

        String queryList = String.format(SEARCH_FILM, filter);
        return findMany(queryList, "%" + queryStr + "%");
    }
}