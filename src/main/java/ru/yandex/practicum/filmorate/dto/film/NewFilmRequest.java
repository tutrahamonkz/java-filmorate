package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

@Data // Аннотация Lombok для автоматической генерации геттеров, сеттеров, toString и других методов
public class NewFilmRequest {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<Genre> genres;
    private Mpa mpa;
}
