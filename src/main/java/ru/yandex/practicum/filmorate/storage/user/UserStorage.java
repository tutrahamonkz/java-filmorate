package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

// Интерфейс для хранения и управления пользователями
public interface UserStorage {

    // Метод для получения всех пользователей из хранилища
    List<User> getUsers();

    // Метод для получения пользователя по его идентификатору
    Optional<User> getUserById(Long id);

    // Метод для создания нового пользователя
    User userCreate(User user);

    // Метод для обновления информации о существующем пользователе
    User userUpdate(User user);

    boolean deleteUser(Long userId);
}