package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data // Аннотация Lombok для автоматической генерации геттеров, сеттеров, toString и других методов
public class UpdateUserRequest {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    // Метод для проверки наличия валидного идентификатора
    public boolean hasId() {
        return ! (id == null || id <= 0);
    }

    // Метод для проверки наличия валидной электронной почты
    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    // Метод для проверки наличия валидного логина
    public boolean hasLogin() {
        return ! (login == null || login.isBlank());
    }

    // Метод для проверки наличия валидного имени
    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    // Метод для проверки наличия даты рождения
    public boolean hasBirthday() {
        return ! (birthday == null);
    }
}