package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenresFilm;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Аннотация, указывающая, что класс является компонентом Spring и будет управляться контейнером Spring
public class GenresFilmRowMapper implements RowMapper<GenresFilm> {
    // Переопределяем метод mapRow для преобразования строки результата запроса в объект GenresFilm
    @Override
    public GenresFilm mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Создаем объект Genre, заполняя его поля значениями из ResultSet
        Genre genre = Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
        // Создаем и возвращаем объект GenresFilm, заполняя его поля значениями из ResultSet
        return GenresFilm.builder()
                .filmId(rs.getLong("film_id"))
                .genre(genre)
                .build();
    }
}