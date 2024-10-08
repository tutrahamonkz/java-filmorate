package ru.yandex.practicum.filmorate.service.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service // Аннотация указывает, что данный класс является сервисом и может быть использован в контексте Spring
public class UserService {

    private final UserStorage userStorage; // Хранение ссылки на объект UserStorage для работы с данными о пользователях

    // Конструктор, принимающий UserStorage в качестве параметра
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Метод для получения всех пользователей из хранилища
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    // Метод для получения пользователя по его идентификатору
    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    // Метод для создания нового пользователя
    public User userCreate(User user) {
        return userStorage.userCreate(user);
    }

    // Метод для обновления существующего пользователя
    public User userUpdate(User user) {
        return userStorage.userUpdate(user);
    }

    // Метод для добавления пользователя в друзья
    public User friending(Long userId, Long friendId) {
        return userStorage.friending(userId, friendId);
    }

    // Метод для удаления пользователя из друзей
    public User unfriending(Long userId, Long friendId) {
        return userStorage.unfriending(userId, friendId);
    }

    // Метод для получения списка друзей указанного пользователя
    public Collection<User> getUserFriends(Long userId) {
        return userStorage.getUserFriends(userId);
    }

    // Метод для получения списка взаимных друзей между двумя пользователями
    public Collection<User> listOfMutualFriends(Long userId, Long friendId) {
        return userStorage.listOfMutualFriends(userId, friendId);
    }

    // Метод для проверки существования пользователя по его идентификатору
    public User checkContainsUserId(Long userId) {
        return userStorage.checkContainsUserId(userId);
    }
}