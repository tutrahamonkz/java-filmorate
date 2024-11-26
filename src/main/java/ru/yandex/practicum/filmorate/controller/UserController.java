package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.feed.FeedDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

@RestController // Указывает, что этот класс является REST-контроллером
@RequestMapping("/users") // Устанавливает базовый путь для всех методов контроллера
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping // Обрабатывает GET-запросы по пути "/users"
    public List<UserDto> findAll() {
        return userService.getUsers(); // Возвращает всех пользователей
    }

    @PostMapping // Обрабатывает POST-запросы по пути "/users"
    public UserDto create(@Valid @RequestBody User user) {
        return userService.userCreate(user); // Создает нового пользователя и возвращает его
    }

    @PutMapping // Обрабатывает PUT-запросы по пути "/users"
    public UserDto update(@RequestBody UpdateUserRequest request) {
        // Обновляет информацию о пользователе и возвращает обновленного пользователя
        return userService.userUpdate(request);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Обрабатывает PUT-запросы для добавления друга
    @PutMapping("/{id}/friends/{friendId}")
    public UserDto friending(@PathVariable Long id, @PathVariable Long friendId) {
        // Добавляет пользователя в друзья и возвращает обновленного пользователя
        return userService.friending(id, friendId);
    }

    // Обрабатывает DELETE-запросы для удаления друга
    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDto unfriending(@PathVariable Long id, @PathVariable Long friendId) {
        // Удаляет пользователя из друзей и возвращает обновленного пользователя
        return userService.unfriending(id, friendId);
    }

    // Обрабатывает GET-запросы для получения списка друзей пользователя
    @GetMapping("/{id}/friends")
    public List<UserDto> getUserFriends(@PathVariable Long id) {
        // Возвращает коллекцию друзей указанного пользователя
        return userService.getUserFriends(id);
    }

    // Обрабатывает GET-запросы для получения списка общих друзей
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> listOfMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        // Возвращает коллекцию общих друзей между двумя пользователями
        return userService.listOfMutualFriends(id, otherId);
    }

    // Обрабатывает DELETE-запросы для удаления пользователя
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/feed")
    public List<FeedDto> getUserFeeds(@PathVariable Long id) {
        // Возвращает ленту новостей указанного пользователя
        return userService.getUserFeed(id);
    }
}