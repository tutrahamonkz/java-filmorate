package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .timestamp(rs.getTimestamp("timestamp"))
                .entityId(rs.getLong("entity_id"))
                .eventType(EventType.valueOf(rs.getString("event_type"))) //преобразование строки в EventType
                .operation(Operation.valueOf(rs.getString("operation")))
                .build();
    }
}
