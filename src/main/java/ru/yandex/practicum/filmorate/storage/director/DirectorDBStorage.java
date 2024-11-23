package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class DirectorDBStorage extends BaseStorage<Director> {
    private static final String FIND_ALL_QUERY = "SELECT dir_id, dir_name FROM directors";
    private static final String INSERT_QUERY = "INSERT INTO directors (dir_name) VALUES (?)";
    private static final String DIRECTOR_QUERY = "SELECT dir_id, dir_name FROM directors WHERE dir_id = ?";
    private static final String UPDATE_QUERY = "UPDATE directors SET dir_name = ? WHERE dir_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE dir_id = ?";


    public DirectorDBStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper, Director.class);
    }

    public List<Director> getDirectors() {
        log.info("Запрос на получение списка режиссеров");
        List<Director> directors = findMany(FIND_ALL_QUERY);
        log.info("Найдено {} режиссеров", directors.size());
        return directors;
    }

    public Director createDirector(Director director) {
        long id = insert(
                INSERT_QUERY,
                director.getName()
        );
        director.setId(id);
        log.info("Создан режиссер с id {} ", id);
        return director;
    }

    public List<Director> getListDirector(List<Director> list) {
        String placeholders = String.join(",", Collections.nCopies(list.size(), "?"));
        String listDirectorQuery = "SELECT dir_id, dir_name FROM directors WHERE dir_id IN (" + placeholders + ")";
        List<Long> listLong = list.stream()
                .map(Director::getId)
                .toList();
        Object[] params = listLong.toArray(new Object[0]);
        log.info("Поиск режиссеров с id {}", params);
        return findMany(listDirectorQuery, params);
    }

    public Optional<Director> getDirectorById(long id) {
        log.info("Поиск режиссера по id {}", id);
        return findOne(DIRECTOR_QUERY, id);
    }

    public void updateDirector(Director director) {
        update(UPDATE_QUERY,
                director.getName(),
                director.getId());
        log.info("Обновлены данные о режиссере с id {}", director.getId());
    }

    public void deleteDirector(long id) {
        update(
                DELETE_QUERY,
                id);
        log.info("Удалены данные о режиссере с id {}", id);
    }

}
