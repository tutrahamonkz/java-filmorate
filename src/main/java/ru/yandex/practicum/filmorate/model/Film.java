package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
public class Film {

    private Long id;
    @NotNull(message = "Название фильма должно быть указано")
    @NotBlank(message = "Название фильма должно быть не пустым и не состоять только из пробелов")
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
}
