package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor // Аннотация Lombok, автоматически генерирующая конструктор с обязательными полями
public class BaseStorage<T> {
    protected final JdbcTemplate jdbc;// JdbcTemplate для выполнения SQL-запросов
    protected final RowMapper<T> mapper; // Mapper для преобразования результатов запроса в объекты типа T
    private final Class<T> entityType; // Класс сущности, используемый для создания объектов типа T

    // Метод для поиска одного объекта по заданному запросу и параметрам
    protected Optional<T> findOne(String query, Object... params) {
        try {
            // Выполняем запрос и возвращаем результат в виде Optional
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) { // Игнорируем исключение, если нет результатов
            return Optional.empty(); // Возвращаем пустой Optional
        }
    }

    // Метод для поиска нескольких объектов по заданному запросу и параметрам
    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, params, mapper); // Выполняем запрос и возвращаем список объектов
    }

    // Метод для вставки нового объекта и возвращения его сгенерированного идентификатора
    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder(); // Хранитель для сгенерированного ключа
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS); // Подготавливаем запрос для вставки
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]); // Устанавливаем параметры в PreparedStatement
            }
            return ps; // Возвращаем PreparedStatement
        }, keyHolder); // Передаем keyHolder для получения сгенерированного ключа

        // Возвращаем сгенерированный ключ
        return Objects.requireNonNull(keyHolder.getKeyAs(Integer.class)).longValue();
    }

    // Метод для обновления существующего объекта по заданному запросу и параметрам
    protected void update(String query, Object... params) {
        // Выполняем обновление и получаем количество обновленных строк
        int rowsUpdate = jdbc.update(query, params);
        if (rowsUpdate == 0) { // Если ничего не обновлено, выбрасываем исключение
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    // Метод для удаления объекта по заданному запросу и параметрам
    protected boolean delete(String query, Object... params) {
        int rowsDeleted = jdbc.update(query, params); // Выполняем обновление и получаем количество удаленных строк
        return rowsDeleted > 0; // Возвращаем true, если строки были удалены
    }

    protected void insertMany(String query) {
        int count = jdbc.update(query);
        if (count > 0) {
            log.info("Данные запроса " + query + " успешно сохранены");
        } else {
            log.info("Данные запроса " + query + " не сохранены (дублируются или другая причина)");
        }
    }

}