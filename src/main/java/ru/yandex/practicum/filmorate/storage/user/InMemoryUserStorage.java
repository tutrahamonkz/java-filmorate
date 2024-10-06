package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j // Аннотация для автоматической генерации логгера
@Component // Аннотация, указывающая, что данный класс является компонентом Spring
public class InMemoryUserStorage implements UserStorage {
    // Хранение пользователей в виде пары "идентификатор - пользователь"
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User userCreate(User user) {
        user.setId(getNextId()); // Генерируем следующий идентификатор
        users.put(user.getId(), user); // Сохраняем пользователя в коллекцию
        log.info("Создан пользователь с id: {}", user.getId()); // Логируем информацию о создании пользователя
        return user; // Возвращаем созданного пользователя
    }

    @Override
    public User userUpdate(User user) {
        checkContainsUserId(user.getId());
        users.put(user.getId(), user); // Обновляем информацию о пользователе
        log.info("Обновлен пользователь с id: {}", user.getId()); // Логируем информацию об обновлении
        return user; // Возвращаем обновленного пользователя
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public User friending(Long userId, Long friendId) {
        checkContainsUserId(userId);
        checkContainsUserId(friendId);
        users.get(userId).getFriends().add(friendId);
        users.get(friendId).getFriends().add(userId);
        log.info("Пользователь с id: {} добавил в друзья пользователя с id: {}", userId, friendId);
        return users.get(userId);

    }

    @Override
    public User unfriending(Long userId, Long friendId) {
        checkContainsUserId(userId);
        checkContainsUserId(friendId);
        users.get(userId).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(userId);
        log.info("Пользователь с id: {} удалил из друзей пользователя с id: {}", userId, friendId);
        return users.get(friendId);
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        checkContainsUserId(userId);
        log.info("Запрошен список друзей пользователя с id: {}", userId);
        return users.get(userId).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<User> listOfMutualFriends(Long userId, Long friendId) {
        checkContainsUserId(userId);
        checkContainsUserId(friendId);
        log.info("Запрошен список общих друзей для пользователей с id: {} и id: {}", userId, friendId);
        return users.get(userId).getFriends().stream()
                .map(id -> {
                    if (users.get(friendId).getFriends().contains(id)) {
                        return users.get(id);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public void checkContainsUserId(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь c id: " + userId + " не найден");
        }
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