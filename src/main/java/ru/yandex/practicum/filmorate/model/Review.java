package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode(of = "reviewId")
@ToString
public class Review {
    private Long reviewId;

    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;

    @NotNull
    private Long userId;

    @NotNull
    private Long filmId;

    @NotNull
    private Boolean isPositive;

    private Integer useful;
}