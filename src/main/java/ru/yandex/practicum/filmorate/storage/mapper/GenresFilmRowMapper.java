package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.GenresFilm;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Аннотация, указывающая, что класс является компонентом Spring и будет управляться контейнером Spring
public class GenresFilmRowMapper implements RowMapper<GenresFilm> {
    // Переопределяем метод mapRow для преобразования строки результата запроса в объект GenresFilm
    @Override
    public GenresFilm mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Создаем и возвращаем объект GenresFilm, заполняя его поля значениями из ResultSet
        return GenresFilm.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .build();
    }
}