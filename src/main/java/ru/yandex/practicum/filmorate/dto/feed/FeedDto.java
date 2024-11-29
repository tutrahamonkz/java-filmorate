package ru.yandex.practicum.filmorate.dto.feed;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.Timestamp;

@Data
@Builder
public class FeedDto {
    private Long eventId;
    private Long userId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) //поле должно быть представлено в JSON как числовое значение
    private Timestamp timestamp;
    private Long entityId;
    private EventType eventType;
    private Operation operation;
}
