package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
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
                    "JOIN MPA_TYPE mp ON f.mpa = mp.mpa_id "+
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
        String queryInsertDir = "INSERT INTO directors_films (film_id, dir_id) VALUES " + valuesBuilder + " ON CONFLICT (dir_id, film_id) DO NOTHING"; ///вставка VALUES в форме (1,2),(1,3),(1,4)
        insertMany(queryInsertDir);

        log.info("Добавлена запись соответствия в таблицу (id фильма, id режиссера) " + valuesBuilder);
    }


    public List<Director> getDirectorsForFilm(Long id) {
        return findManyDirectors(SEARCH_DIR_FOR_FILM_QUERY, dirMapper, id);
    }

    public List<Film> getSortedFilmsByYear(Long id) {
        return findManyFIlms(FILM_SORTED_BY_YEAR_QUERY, filmMapper, id);
    }

    public List<Film> getSortedFilmsByLikes(Long id) {
        return findManyFIlms(FILM_SORTED_BY_LIKE_QUERY, filmMapper, id);
    }

    public List<Director> findManyDirectors(String query, RowMapper<Director> mapper, Object... params) { //поиск режиссеров с именами в двух базах
        return jdbc.query(query, params, mapper);
    }

    public List<Film> findManyFIlms(String query, RowMapper<Film> mapper, Object... params) {
        return jdbc.query(query, params, mapper);
    }

    private int insertMany(String query) {
        int count = jdbc.update(query);
        if (count > 0) {
            return count;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

}

