package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Slf4j // Аннотация для автоматической генерации логгера
@Repository // Аннотация, указывающая, что класс является репозиторием
public class MpaDbStorage extends BaseStorage<Mpa> {
    // SQL-запросы для работы с таблицей MPA_TYPE
    private static final String FIND_ALL_QUERY = "SELECT * FROM MPA_TYPE ORDER BY MPA_ID";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM MPA_TYPE WHERE MPA_ID = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper, Mpa.class);
    }

    // Метод для получения всех MPA
    public List<Mpa> getAllMpa() {
        log.info("Запрос на получение всех MPA"); // Логируем запрос на получение всех MPA
        List<Mpa> mpaList = findMany(FIND_ALL_QUERY); // Выполняем запрос и получаем список MPA
        log.info("Получено {} MPA", mpaList.size()); // Логируем количество полученных MPA
        return mpaList; // Возвращаем список MPA
    }

    // Метод для получения MPA по ID
    public Optional<Mpa> getMpaById(Long id) {
        log.info("Запрос на получение MPA с id: {}", id); // Логируем запрос на получение MPA по ID
        return findOne(FIND_BY_ID_QUERY, id); // Выполняем запрос и возвращаем результат в виде Optional
    }
}