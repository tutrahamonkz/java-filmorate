package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
@Repository
public class DirectorDBStorage extends BaseStorage<Director> {
    private static final String FIND_ALL_QUERRY = "SELECT dir_id, dir_name FROM directors";
private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";
//private static final String INSERT_QUERY_WITH_ID = "INSERT INTO directors  "
    public DirectorDBStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper, Director.class);
    }

    public List<Director> getDirectors() {
        log.info("Запрос на получение списка режиссеров");
        List<Director> directors = findMany(FIND_ALL_QUERRY);
        log.info("Найдено {} режиссеров", directors.size());
        return directors;
    }

   /* public Director createDirector(Director director) {

    }*/

}
