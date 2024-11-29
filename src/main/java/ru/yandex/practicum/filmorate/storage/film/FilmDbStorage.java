package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
            "duration = ?, mpa = ? WHERE film_id = ?"; //*mpa
    private static final String FIND_POPULAR_LIMIT_QUERY = "SELECT f.*, mp.MPA_NAME FROM FILMS f " +
            "LEFT JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
            "JOIN MPA_TYPE mp ON f.mpa = mp.MPA_ID " +
            "GROUP BY f.FILM_ID " +
            "ORDER BY COUNT(l.USER_ID) DESC " +
            "LIMIT ?;";
    private static final String DELETE_QUERY = "DELETE FROM FILMS WHERE FILM_ID = ?";

    private static final String FILM_SORTED_BY_YEAR_QUERY =
            "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa, mp.mpa_name " +
                    " FROM directors_films df " +
                    "JOIN films f " +
                    "ON df.film_id = f.film_id " +
                    "JOIN MPA_TYPE mp ON f.mpa = mp.mpa_id " +
                    "WHERE df.dir_id = ? " +
                    "ORDER BY f.release_date ASC";

    private static final String FILM_SORTED_BY_LIKE_QUERY =
            "SELECT film_id, film_name, description, release_date, duration, mpa, mpa_name " +
                    "FROM( " +
                    "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa, mp.mpa_name, COUNT(l.user_id) AS like_count " +
                    "FROM directors_films df " +
                    "JOIN films f ON df.film_id = f.film_id " +
                    "JOIN MPA_TYPE mp ON f.mpa = mp.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id=l.film_id " +
                    "WHERE df.dir_id = ? " +
                    "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa, mp.mpa_name " +
                    ") AS film_likes " +
                    "ORDER BY like_count DESC";
    private static final String SEARCH_FILM = """
            SELECT
                film.film_id, film.film_name, film.description, film.release_date, film.duration, film.mpa,
                m_type.mpa_name,
                dir.dir_id, dir.dir_name,
                COUNT(DISTINCT f_like.user_id) as likes_count
            FROM films AS film
            LEFT JOIN mpa_type AS m_type ON film.mpa = m_type.mpa_id
            LEFT JOIN likes AS f_like ON film.film_id = f_like.film_id
            LEFT JOIN directors_films AS d_film ON film.film_id = d_film.film_id
            LEFT JOIN directors AS dir ON d_film.dir_id = dir.dir_id
            %s
            GROUP BY film.film_id
            ORDER BY likes_count DESC;
            """;
    private static final String COMMON_USERS_FILMS = """
            SELECT
                film.film_id, film.film_name, film.description, film.release_date, film.duration, film.mpa,
                m_type.mpa_name,
                dir.dir_id, dir.dir_name,
                COUNT(DISTINCT first_likes.user_id) as likes_count
            FROM films AS film
            LEFT JOIN mpa_type AS m_type ON film.mpa = m_type.mpa_id
            LEFT JOIN directors_films AS d_film ON film.film_id = d_film.film_id
            LEFT JOIN directors AS dir ON d_film.dir_id = dir.dir_id
            JOIN likes AS first_likes ON film.film_id = first_likes.film_id
            JOIN likes AS second_likes ON film.film_id = second_likes.film_id
            WHERE first_likes.user_id = ? AND second_likes.user_id = ?
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
                film.getMpa().getId(),
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

    // Метод для удаления фильма
    @Override
    public boolean deleteFilm(Long filmId) {
        log.info("Удаление фильма: filmId={}", filmId); // Логируем удаление фильма
        // Выполняем SQL-запрос на удаление записи о фильме из БД
        boolean result = delete(DELETE_QUERY, filmId);
        if (result) {
            log.info("Фильм успешно удален: filmId={}", filmId); // Логируем успешное удаление
            return true;
        } else {
            throw new InternalServerException("Не удалось удалить фильм с id: " + filmId);
        }
    }

    public List<Film> getSortedFilmsByYear(Long id) {
        log.info("Запрос на составление списка фильмов по годам для режиссера с id " + id);
        return findMany(FILM_SORTED_BY_YEAR_QUERY, id);
    }

    public List<Film> getSortedFilmsByLikes(Long id) {
        log.info("Запрос на составление списка фильмов по числу лайков для режиссера с id " + id);
        return findMany(FILM_SORTED_BY_LIKE_QUERY, id);
    }

    @Override
    public List<Film> search(String queryStr, List<String> by) {
        List<String> filterList = new ArrayList<>();
        List<String> params = new ArrayList<>();
        /*
         Поиск разрешен только по названию фильма и имени режиссера
         */
        if (by.contains("title")) {
            filterList.add("LOWER(film.film_name) LIKE LOWER(?)");
            params.add("%" + queryStr + "%");
        }
        if (by.contains("director")) {
            filterList.add("LOWER(dir.dir_name) LIKE LOWER(?)");
            params.add("%" + queryStr + "%");
        }

        if (filterList.isEmpty()) {
            /*
             Если параметр by не содержит нужного ключа - выход с ошибкой
             */
            throw new BadRequestException("Ошибка запроса");
        }

        /*
        Подставляем фильтр в строку запроса
         */
        String queryList = String.format(SEARCH_FILM, "WHERE " + String.join(" OR ", filterList));
        return findMany(queryList, params.toArray());
    }

    @Override
    public List<Film> commonFilms(Long userId, Long friendId) {
        return findMany(COMMON_USERS_FILMS, List.of(userId, friendId).toArray());
    }
}