package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;

@Slf4j // Аннотация для автоматической генерации логгера
@RestController // Указывает, что этот класс является REST-контроллером
@RequestMapping("/users") // Устанавливает базовый путь для всех методов контроллера
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping // Обрабатывает GET-запросы по пути "/users"
    public Collection<User> findAll() {
        return userService.getUsers(); // Возвращает всех пользователей
    }

    @PostMapping // Обрабатывает POST-запросы по пути "/users"
    public User create(@Valid @RequestBody User user) {
            return userService.userCreate(user); // Создает нового пользователя и возвращает его
    }

    @PutMapping // Обрабатывает PUT-запросы по пути "/users"
    public User update(@Valid @RequestBody User newUser) {
        // Обновляет информацию о пользователе и возвращает обновленного пользователя
        return userService.userUpdate(newUser);
    }

    // Обрабатывает PUT-запросы для добавления друга
    @PutMapping("/{id}/friends/{friendId}")
    public User friending(@PathVariable Long id, @PathVariable Long friendId) {
        // Добавляет пользователя в друзья и возвращает обновленного пользователя
        return userService.friending(id, friendId);
    }

    // Обрабатывает DELETE-запросы для удаления друга
    @DeleteMapping("/{id}/friends/{friendId}")
    public User unfriending(@PathVariable Long id, @PathVariable Long friendId) {
        // Удаляет пользователя из друзей и возвращает обновленного пользователя
        return userService.unfriending(id, friendId);
    }

    // Обрабатывает GET-запросы для получения списка друзей пользователя
    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Long id) {
        // Возвращает коллекцию друзей указанного пользователя
        return userService.getUserFriends(id);
    }

    // Обрабатывает GET-запросы для получения списка общих друзей
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> listOfMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        // Возвращает коллекцию общих друзей между двумя пользователями
        return userService.listOfMutualFriends(id, otherId);
    }
}