package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Feed {
    private Long eventId; //id новости
    private Long userId; //id пользователя для которого собирается новость
    private Timestamp timestamp;
    private Long entityId; // идентификатор сущности, с которой произошло событие
    private EventType eventType; // одно из значений LIKE, REVIEW или FRIEND - тип ENUM
    private Operation operation; // одно из значений REMOVE, ADD, UPDATE - тип ENUM
}
