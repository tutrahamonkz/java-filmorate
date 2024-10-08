package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override  // Метод для получения пользователя по его идентификатору
    public User getUserById(Long id) {
        return checkContainsUserId(id); // Возвращаем пользователя по его идентификатору
    }

    @Override // Метод для добавления пользователя в друзья
    public User friending(Long userId, Long friendId) {
        // Проверяем существование обоих пользователей по их идентификаторам
        User user = checkContainsUserId(userId);
        User friend = checkContainsUserId(friendId);

        // Добавляем friendId в список друзей пользователя userId и наоборот
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        // Логируем действие добавления в друзья
        log.info("Пользователь с id: {} добавил в друзья пользователя с id: {}", userId, friendId);
        return users.get(userId); // Возвращаем обновленного пользователя userId

    }

    @Override // Метод для удаления пользователя из друзей
    public User unfriending(Long userId, Long friendId) {
        // Проверяем существование обоих пользователей по их идентификаторам
        User user = checkContainsUserId(userId);
        User friend = checkContainsUserId(friendId);

        // Удаляем friendId из списка друзей пользователя userId и наоборот
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        // Логируем действие удаления из друзей
        log.info("Пользователь с id: {} удалил из друзей пользователя с id: {}", userId, friendId);
        return users.get(friendId); // Возвращаем обновленного пользователя friendId
    }

    @Override  // Метод для получения списка друзей указанного пользователя
    public Collection<User> getUserFriends(Long userId) {
        User user = checkContainsUserId(userId); // Проверяем существование пользователя по его идентификатору

        // Логируем запрос на получение списка друзей пользователя
        log.info("Запрошен список друзей пользователя с id: {}", userId);

        // Возвращаем коллекцию друзей пользователя userId
        return user.getFriends().stream()
                .map(users::get)
                .collect(Collectors.toSet());
    }

    @Override // Метод для получения списка взаимных друзей между двумя пользователями
    public Collection<User> listOfMutualFriends(Long userId, Long friendId) {
        // Проверяем существование обоих пользователей по их идентификаторам
        User user = checkContainsUserId(userId);
        User friend = checkContainsUserId(friendId);

        // Получаем список идентификаторов общих друзей
        List<Long> commonsFriendsId = user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .toList();

        // Логируем запрос на получение списка общих друзей
        log.info("Запрошен список общих друзей для пользователей с id: {} и id: {}", userId, friendId);

        // Возвращает коллекцию общих друзей между userId и friendId
        return commonsFriendsId.stream()
                .map(users::get)
                .toList();
    }

    @Override // Метод для проверки существования пользователя по его идентификатору
    public User checkContainsUserId(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь c id: " + userId + " не найден");
        }
        return users.get(userId);
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