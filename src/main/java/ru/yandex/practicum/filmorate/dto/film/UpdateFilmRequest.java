package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;

import java.time.LocalDate;

@Data // Аннотация Lombok для автоматической генерации геттеров, сеттеров, toString и других методов
public class UpdateFilmRequest {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    // Метод для проверки наличия валидного идентификатора
    public boolean hasId() {
        return ! (id == null || id <= 0);
    }

    // Метод для проверки наличия валидного названия
    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    // Метод для проверки наличия валидного описания
    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    // Метод для проверки наличия валидной даты выхода
    public boolean hasReleaseDate() {
        return ! (name == null);
    }

    // Метод для проверки наличия валидной продолжительности
    public boolean hasDuration() {
        return ! (duration == null || duration <= 0);
    }
}