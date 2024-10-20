package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data // Генерирует геттеры, сеттеры, методы equals, hashCode и toString
@Builder // Позволяет использовать паттерн Builder для создания объектов класса
public class GenresFilm {
    private Long filmId; // Идентификатор фильма, к которому относится жанр
    private Long genreId; // Идентификатор жанра
}
