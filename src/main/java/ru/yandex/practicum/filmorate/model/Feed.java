package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Feed {
    @Builder.Default
    private Long eventId = null; //id новости
    private Long userId; //id пользователя для которого собирается новость
    private Timestamp timestamp;
    private Long entityId; // идентификатор сущности, с которой произошло событие (id другa, которого добавили, id фильма, который лайкнули)
    private EventType eventType; // одно из значений LIKE, REVIEW или FRIEND - тип ENUM
    private Operation operation; // одно из значений REMOVE, ADD, UPDATE - тип ENUM
}
