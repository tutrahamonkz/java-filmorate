package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validate.MinDate;

import java.time.LocalDate;

@Getter
@Setter
public class Film {

    private static final int MAXIMUM_DESCRIPTION_LENGTH = 200;

    private Long id;
    @NotNull(message = "Название фильма должно быть указано")
    @NotBlank(message = "Название фильма должно быть не пустым и не состоять только из пробелов")
    private String name;
    @Size(max = MAXIMUM_DESCRIPTION_LENGTH, message = "Длинна описания не должна превышать " +
            MAXIMUM_DESCRIPTION_LENGTH + " символов")
    private String description;
    @MinDate(message = "Дата релиза не может быть раньше: {value}")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;
}
