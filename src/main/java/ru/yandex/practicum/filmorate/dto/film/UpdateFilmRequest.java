package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

@Data // Аннотация Lombok для автоматической генерации геттеров, сеттеров, toString и других методов
public class UpdateFilmRequest {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa;
    private List<Genre> genres;
    private List<Director> directors;

    // Метод для проверки наличия валидного идентификатора
    public boolean hasId() {
        return !(id == null || id <= 0);
    }

    // Метод для проверки наличия валидного названия
    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    // Метод для проверки наличия валидного описания
    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    // Метод для проверки наличия валидной даты выхода
    public boolean hasReleaseDate() {
        return !(name == null);
    }

    // Метод для проверки наличия валидной продолжительности
    public boolean hasDuration() {
        return !(duration == null || duration <= 0);
    }

    public boolean hasMpa() {
        return !(mpa == null);
    }

    public boolean hasGenre() {
        return !(genres == null);
    }

    public boolean hasDirectors() {
        return !(directors == null);
    }

}