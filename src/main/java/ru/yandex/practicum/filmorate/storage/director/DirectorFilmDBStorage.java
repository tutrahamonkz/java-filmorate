package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.List;

@Slf4j
@Repository
public class DirectorFilmDBStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Film> filmMapper;
    private final RowMapper<Director> dirMapper;


    public DirectorFilmDBStorage(JdbcTemplate jdbc, RowMapper<Film> filmMapper, RowMapper<Director> dirMapper) {
        this.jdbc = jdbc;
        this.filmMapper = filmMapper;
        this.dirMapper = dirMapper;

    }

    private static final String SEARCH_DIR_FOR_FILM_QUERY = "SELECT df.dir_id, d.dir_name " +
            "FROM directors_films df " +
            "JOIN directors d " +
            "ON d.dir_id=df.dir_id  " +
            "WHERE df.film_id = ?";

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

    private static final String DELETE_DIRECTOR_BY_FILM_QUERY =
            "DELETE FROM directors_films WHERE film_id = ?";

    public void createPost(Film film) {
        List<Long> listLong = film.getDirectors().stream()
                .map(Director::getId)
                .toList();
        StringBuilder valuesBuilder = new StringBuilder();
        for (long idDir : listLong) {
            if (!valuesBuilder.isEmpty()) {
                valuesBuilder.append(", ");
            }
            valuesBuilder.append("(").append(film.getId()).append(", ").append(idDir).append(")");
        }
        String queryInsertDir = "MERGE INTO directors_films  AS df USING (VALUES " + valuesBuilder + " ) " + //вставка VALUES в форме (1,2),(1,3),(1,4)
                "AS s (film_id, dir_id) ON df.film_id=s.film_id AND df.dir_id = s.dir_id " +
                "WHEN NOT MATCHED THEN " +
                "INSERT (film_id, dir_id) VALUES (s.film_id, s.dir_id)";
        insertMany(queryInsertDir);
        log.info("Добавлена запись соответствия в таблицу (id фильма, id режиссера) " + valuesBuilder);
    }


    public List<Director> getDirectorsForFilm(Long id) {
        log.info("Поиск режиссеров для фильма с id " + id);
        return findManyDirectors(SEARCH_DIR_FOR_FILM_QUERY, dirMapper, id);
    }

    public List<Film> getSortedFilmsByYear(Long id) {
        log.info("Запрос на составление списка фильмов по годам для режиссера с id " + id);
        return findManyFIlms(FILM_SORTED_BY_YEAR_QUERY, filmMapper, id);
    }

    public List<Film> getSortedFilmsByLikes(Long id) {
        log.info("Запрос на составление списка фильмов по числу лайков для режиссера с id " + id);
        return findManyFIlms(FILM_SORTED_BY_LIKE_QUERY, filmMapper, id);
    }

    public void deleteFilmDirector(Long id) {
        log.info("Удаление режиссера с id " + id);
        update(DELETE_DIRECTOR_BY_FILM_QUERY, id);
    }

    private List<Director> findManyDirectors(String query, RowMapper<Director> mapper, Object... params) { //поиск режиссеров с именами в двух базах
        return jdbc.query(query, params, mapper);
    }

    private List<Film> findManyFIlms(String query, RowMapper<Film> mapper, Object... params) {
        return jdbc.query(query, params, mapper);
    }

    private void insertMany(String query) {
        int count = jdbc.update(query);
        if (count > 0) {
            log.info("Данные запроса " + query + " успешно сохранены");
        } else {
            log.info("Данные запроса " + query + " не сохранены (дублируются или другая причина)");
            ;
        }
    }

    private void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            log.info("Данные не обновлены (нет записи)");

        }
    }
}

