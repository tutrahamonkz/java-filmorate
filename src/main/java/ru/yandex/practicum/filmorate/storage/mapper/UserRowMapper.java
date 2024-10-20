package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Аннотация, указывающая, что класс является компонентом Spring и будет управляться контейнером Spring
public class UserRowMapper implements RowMapper<User> {
    // Переопределяем метод mapRow для преобразования строки результата запроса в объект User
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Создаем и возвращаем объект User, заполняя его поля значениями из ResultSet
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                // Получаем значение birthday и преобразуем его в LocalDate
                .birthday(rs.getTimestamp("birthday").toLocalDateTime().toLocalDate())
                .build();
    }
}