package ru.yandex.practicum.filmorate.dto.film;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data // Аннотация Lombok для автоматической генерации геттеров, сеттеров, toString и других методов
@Builder // Аннотация @Builder позволяет использовать паттерн "строитель" для создания объектов класса
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Long> likes;
    private List<Genre> genres;
    private Mpa mpa;
}