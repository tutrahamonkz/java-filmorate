package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Аннотация, указывающая, что класс является компонентом Spring и будет управляться контейнером Spring
public class MpaRowMapper implements RowMapper<Mpa> {
    // Переопределяем метод mapRow для преобразования строки результата запроса в объект Mpa
    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Создаем и возвращаем объект Mpa, заполняя его поля значениями из ResultSet
        return Mpa.builder()
                .id(rs.getLong("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}