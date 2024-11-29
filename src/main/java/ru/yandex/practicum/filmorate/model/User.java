package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data // Аннотация @Data генерирует геттеры, сеттеры, toString, equals и hashCode для класса
@Builder // Аннотация @Builder позволяет использовать паттерн "строитель" для создания объектов класса
public class User {
    private Long id;

    // Email пользователя, должен быть указан и соответствовать формату email
    @NotNull(message = "Имейл должен быть указан")
    @Email(message = "Имейл должен содержать символ '@'")
    private String email;

    // Логин пользователя, должен быть указан и не содержать пробелов
    @NotNull(message = "Логин должен быть указан")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать символ пробела")
    private String login;

    private String name;

    // Дата рождения пользователя, должна быть в прошлом
    @Past(message = "Дата рождения не должна быть позже текущей даты")
    private LocalDate birthday;

    // Множество идентификаторов друзей пользователя
    private final List<Long> friends = new ArrayList<>();

    // Если имя не указано, возвращается логин
    public String getName() {
        if (name.isBlank()) {
            return login;
        }
        return name;
    }
}