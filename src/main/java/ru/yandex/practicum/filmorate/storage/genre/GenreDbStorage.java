package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Slf4j // Аннотация для включения логирования в класс
@Repository // Аннотация, указывающая, что класс является репозиторием Spring
public class GenreDbStorage extends BaseStorage<Genre> {
    // SQL-запросы для работы с таблицей GENRE_TYPE
    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRE_TYPE";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRE_TYPE WHERE GENRE_ID = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    // Метод для получения списка всех жанров
    public List<Genre> getAllGenres() {
        log.info("Запрос всех жанров"); // Логируем запрос на получение всех жанров
        List<Genre> genres = findMany(FIND_ALL_QUERY); // Выполняем SQL-запрос для получения всех жанров
        log.info("Найдено {} жанров", genres.size()); // Логируем количество найденных жанров
        return genres; // Возвращаем список найденных жанров
    }

    // Метод для получения жанра по его ID
    public Optional<Genre> getGenreById(Long id) {
        log.info("Запрос жанра с ID: {}", id); // Логируем запрос на получение жанра по ID
        return findOne(FIND_BY_ID_QUERY, id); // Выполняем SQL-запрос и возвращаем результат в виде Optional
    }
}