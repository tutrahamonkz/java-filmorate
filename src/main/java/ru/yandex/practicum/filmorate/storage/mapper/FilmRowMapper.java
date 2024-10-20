package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Аннотация, указывающая, что класс является компонентом Spring и будет управляться контейнером Spring
public class FilmRowMapper implements RowMapper<Film> {
    // Переопределяем метод mapRow для преобразования строки результата запроса в объект Film
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Создаем объект Mpa, заполняя его поля значениями из ResultSet
        Mpa mpa = Mpa.builder()
                .id(rs.getLong("mpa"))
                .name(rs.getString("mpa_name"))
                .build();
        // Создаем и возвращаем объект Film, заполняя его поля значениями из ResultSet
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                // Получаем значение releaseDate и преобразуем его в LocalDate
                .releaseDate(rs.getTimestamp("release_date").toLocalDateTime().toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa) // Устанавливаем объект Mpa в поле mpa объекта Film
                .build();
    }
}