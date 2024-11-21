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

    private static final String SEARCH_DIR_FOR_FILM = "SELECT df.dir_id, d.dir_name " +
            "FROM directors_films df " +
            "JOIN directors d " +
            "ON d.dir_id=df.dir_id  " +
            "WHERE df.film_id = ?";

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
        String queryInsertDir = "INSERT INTO directors_films (film_id, dir_id) VALUES " + valuesBuilder; ///вставка VALUES в форме (1,2),(1,3),(1,4)
        insertMany(queryInsertDir);

        log.info("Добавлена запись соответствия в таблицу (id фильма, id режиссера) " + valuesBuilder);
    }


    public List<Director> getDirectorsForFilm(Long id) {
        return findManyDirectors(SEARCH_DIR_FOR_FILM, dirMapper, id);
    }

    public List<Director> findManyDirectors(String query, RowMapper<Director> mapper, Object... params) { //поиск режиссеров с именами в двух базах
        return jdbc.query(query, params, mapper);
    }

    protected int insertMany(String query) {
        int count = jdbc.update(query);
        if (count > 0) {
            return count;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

}

