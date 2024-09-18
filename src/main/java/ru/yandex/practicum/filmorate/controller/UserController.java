package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j // Аннотация для автоматической генерации логгера
@RestController // Указывает, что этот класс является REST-контроллером
@RequestMapping("/users") // Устанавливает базовый путь для всех методов контроллера
public class UserController {
    // Хранение пользователей в виде пары "идентификатор - пользователь"
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping // Обрабатывает GET-запросы по пути "/users"
    public Collection<User> findAll() {
        return users.values(); // Возвращает всех пользователей
    }

    @PostMapping // Обрабатывает POST-запросы по пути "/users"
    public User create(@Valid @RequestBody User user) {
            user.setId(getNextId()); // Генерируем следующий идентификатор
            users.put(user.getId(), user); // Сохраняем пользователя в коллекцию
            log.info("Создан пользователь с id: {}", user.getId()); // Логируем информацию о создании пользователя
            return user; // Возвращаем созданного пользователя
    }

    @PutMapping // Обрабатывает PUT-запросы по пути "/users"
    public User update(@Valid @RequestBody User newUser) {
        // Проверяем, существует ли пользователь с указанным идентификатором
        if (users.containsKey(newUser.getId())) {
                users.put(newUser.getId(), newUser); // Обновляем информацию о пользователе
                log.info("Обновлен пользователь с id: {}", newUser.getId()); // Логируем информацию об обновлении
                return newUser; // Возвращаем обновленного пользователя
        }
        // Если пользователь не найден, выбрасываем исключение NotFoundException
        throw new NotFoundException("Пользователь с id: " + newUser.getId() + " не найден");
    }

    // Метод для генерации следующего идентификатора
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}