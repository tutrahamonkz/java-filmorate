package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Аннотация, указывающая, что класс является компонентом Spring и будет управляться контейнером Spring
public class GenreRowMapper implements RowMapper<Genre> {
    // Переопределяем метод mapRow для преобразования строки результата запроса в объект Genre
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Создаем и возвращаем объект Genre, заполняя его поля значениями из ResultSet
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}