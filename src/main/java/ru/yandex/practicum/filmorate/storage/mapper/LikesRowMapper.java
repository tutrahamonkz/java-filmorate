package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Аннотация, указывающая, что класс является компонентом Spring и будет управляться контейнером Spring
public class LikesRowMapper implements RowMapper<Like> {
    // Переопределяем метод mapRow для преобразования строки результата запроса в объект Like
    @Override
    public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Создаем и возвращаем объект Like, заполняя его поля значениями из ResultSet
        return Like.builder()
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .build();
    }
}