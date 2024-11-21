package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    private Long id;
    @NotNull(message = "Имя режиссера должно быть указано")
    private String name;
}
