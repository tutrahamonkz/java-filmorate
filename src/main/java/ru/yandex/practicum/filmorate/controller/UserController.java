package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (validate(user)) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            user.setId(getNextId());
            users.put(user.getId(), user);
            return user;
        }
        throw new ValidationException("Такой пользователь не может быть создан");
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (users.containsKey(newUser.getId())) {
            if (validate(newUser)) {
                if (newUser.getName() == null) {
                    newUser.setName(newUser.getLogin());
                }
                users.put(newUser.getId(), newUser);
                return newUser;
            }
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private boolean validate(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Имейл должен быть указан и содержать символ '@'");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин должен быть указан и не содержать пробелов");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не должна быть позже текущей даты");
        }
        return true;
    }
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
