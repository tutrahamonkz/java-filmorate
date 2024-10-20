package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter // Генерирует геттер для поля error
public class ErrorResponse {
    private final String error; // Поле для хранения сообщения об ошибке

    public ErrorResponse(String error) {
        this.error = error;
    }
}
