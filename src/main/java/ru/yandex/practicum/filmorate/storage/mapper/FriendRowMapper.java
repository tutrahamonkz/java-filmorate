package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Аннотация, указывающая, что класс является компонентом Spring и будет управляться контейнером Spring
public class FriendRowMapper implements RowMapper<Friendship> {
    // Переопределяем метод mapRow для преобразования строки результата запроса в объект Friendship
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Создаем и возвращаем объект Friendship, заполняя его поля значениями из ResultSet
        return Friendship.builder()
                .userId(rs.getLong("user_id"))
                .friendId(rs.getLong("friend_id"))
                .accept(rs.getBoolean("accept"))
                .build();
    }
}