package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import lombok.Builder;
import lombok.Data;

@Data // Генерирует геттеры, сеттеры, методы equals, hashCode и toString
@Builder // Позволяет использовать паттерн Builder для создания объектов класса
public class Mpa {
    @Max(message = "id не может быть больше 5", value = 5) // Валидация: id не должен превышать 5
    private Long id; // Идентификатор возрастного рейтинга
    private String name;
}