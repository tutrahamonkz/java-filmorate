package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data // Генерирует геттеры, сеттеры, методы equals, hashCode и toString
@Builder // Позволяет использовать паттерн Builder для создания объектов класса
public class Friendship {
    //добавить id
    @Builder.Default
    private Long id = null;
    private Long userId;
    private Long friendId;
    private boolean accept;
}