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

    // Метод для добавления пользователя в друзья
    User friending(Long userId, Long friendId);

    // Метод для удаления пользователя из друзей
    User unfriending(Long userId, Long friendId);

    // Метод для получения списка друзей указанного пользователя
    List<User> getUserFriends(Long userId);

    // Метод для получения списка взаимных друзей между двумя пользователями
    List<User> listOfMutualFriends(Long userId, Long friendId);

    // Метод для проверки существования пользователя по его идентификатору
    User checkContainsUserId(Long userId);
}