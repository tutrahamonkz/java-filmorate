package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validate.MinDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data // Аннотация @Data генерирует геттеры, сеттеры, toString, equals и hashCode для класса
@Builder // Аннотация @Builder позволяет использовать паттерн "строитель" для создания объектов класса
public class Film {

    public static final int MAXIMUM_DESCRIPTION_LENGTH = 200;
    private static final String MIN_RELEASE_DATE = "1895-12-28";

    private Long id;

    // Название фильма, не должно быть пустым или состоять только из пробелов
    @NotNull(message = "Название фильма должно быть указано")
    @NotBlank(message = "Название фильма должно быть не пустым и не состоять только из пробелов")
    private String name;

    // Описание фильма, ограниченное максимальной длиной
    @Size(max = MAXIMUM_DESCRIPTION_LENGTH, message = "Длинна описания не должна превышать " +
            MAXIMUM_DESCRIPTION_LENGTH + " символов")
    private String description;

    // Дата релиза фильма, должна быть не раньше указанной минимальной даты
    @MinDate(message = "Дата релиза не может быть раньше: {value}", value = MIN_RELEASE_DATE)
    private LocalDate releaseDate;

    // Продолжительность фильма, не должна быть отрицательной
    @PositiveOrZero(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    // Множество идентификаторов пользователей, которые поставили лайк фильму
    private final Set<Long> likes = new HashSet<>();


    // Список жанров фильма
    private List<@Valid Genre> genres;

    // рейтинг MPA фильма
    @Valid
    private Mpa mpa;
}