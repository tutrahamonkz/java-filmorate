package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data // Генерирует геттеры, сеттеры, методы equals, hashCode и toString
@Builder // Позволяет использовать паттерн Builder для создания объектов класса
public class Genre {

    @Max(message = "id не может быть больше 6", value = 6) // Валидация: id не должен превышать 6
    private Long id; // Идентификатор жанра
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(id, genre.id) && Objects.equals(name, genre.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}