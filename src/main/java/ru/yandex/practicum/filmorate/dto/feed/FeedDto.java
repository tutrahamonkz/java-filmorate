package ru.yandex.practicum.filmorate.dto.feed;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.Timestamp;

@Data
@Builder
public class FeedDto {
    private Long eventId;
    private Timestamp timestamp;
    private Long entityId;
    private EventType eventType;
    private Operation operation;
}
