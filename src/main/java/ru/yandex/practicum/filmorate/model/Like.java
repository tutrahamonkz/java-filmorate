package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data // Генерирует геттеры, сеттеры, методы equals, hashCode и toString
@Builder // Позволяет использовать паттерн Builder для создания объектов класса
public class Like {
    @NonNull // Аннотация указывает, что поле не может быть null
    private Long filmId; // Идентификатор фильма, к которому относится лайк
    @NonNull // Аннотация указывает, что поле не может быть null
    private Long userId; // Идентификатор пользователя, который поставил лайк
}