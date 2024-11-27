package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;


import java.util.List;

@Slf4j
@Repository
public class DirectorFilmDBStorage extends BaseStorage<Film> {

    public DirectorFilmDBStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    private static final String DELETE_DIRECTOR_BY_FILM_QUERY =
            "DELETE FROM directors_films WHERE film_id = ?";
    private static final String CREATE_POST_DIRECTOR_FILM = "MERGE INTO directors_films  AS df USING (VALUES %s) " + //вставка VALUES в форме (1,2),(1,3),(1,4)
            "AS s (film_id, dir_id) ON df.film_id=s.film_id AND df.dir_id = s.dir_id " +
            "WHEN NOT MATCHED THEN " +
            "INSERT (film_id, dir_id) VALUES (s.film_id, s.dir_id)";

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
        String queryInsertDir = String.format(CREATE_POST_DIRECTOR_FILM, valuesBuilder);
        insertMany(queryInsertDir);
        log.info("Добавлена запись соответствия в таблицу (id фильма, id режиссера) " + valuesBuilder);
    }

    public void deleteFilmDirector(Long id) {
        log.info("Удаление режиссера с id " + id);
        delete(DELETE_DIRECTOR_BY_FILM_QUERY, id);
    }
}

