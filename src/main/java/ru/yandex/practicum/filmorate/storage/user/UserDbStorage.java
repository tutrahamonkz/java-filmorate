package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j // Аннотация для автоматического создания логгера
@Repository // Аннотация, указывающая, что класс является репозиторием
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    // SQL-запросы для работы с таблицей USERS
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO USERS(login, email, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET login = ?, email = ?, name = ?, birthday = ?" +
            " WHERE user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM USERS WHERE USER_ID = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    // Метод для получения всех пользователей
    @Override
    public List<User> getUsers() {
        log.info("Запрос на получение всех пользователей"); // Логируем запрос на получение всех пользователей
        List<User> users = findMany(FIND_ALL_QUERY); // Получаем список всех пользователей из базы данных
        log.info("Получено {} пользователей", users.size()); // Логируем количество полученных пользователей
        return users;
    }

    // Метод для получения пользователя по id
    @Override
    public Optional<User> getUserById(Long userId) {
        // Логируем запрос на получение пользователя по ID
        log.info("Запрос на получение пользователя с id: {}", userId);
        return findOne(FIND_BY_ID_QUERY, userId); // Получаем пользователя по его ID
    }

    // Метод для создания пользователя
    @Override
    public User userCreate(User user) {
        log.info("Создание пользователя: {}", user); // Логируем информацию о создаваемом пользователе
        Long id = insert(
                INSERT_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay()) // Преобразуем дату рождения в Timestamp
        );
        user.setId(id); // Устанавливаем сгенерированный ID пользователю
        log.info("Создан пользователь с id: {}", id); // Логируем информацию о создании пользователя
        return user; // Возвращаем созданного пользователя
    }

    // Метод для обновления пользователя
    @Override
    public User userUpdate(User user) {
        log.info("Обновление пользователя с id: {}", user.getId()); // Логируем информацию об обновлении пользователя
        update(
                UPDATE_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay()), // Преобразуем дату рождения в Timestamp
                user.getId() // Передаем ID пользователя для обновления
        );
        log.info("Обновлен пользователь с id: {}", user.getId()); // Логируем информацию об обновлении
        return user; // Возвращаем обновленного пользователя
    }

    // Метод для удаления пользователя
    @Override
    public boolean deleteUser(Long userId) {
        log.info("Удаление пользователя: userId={}", userId); // Логируем удаление фильма
        // Выполняем SQL-запрос на удаление записи о пользователе из БД
        boolean result = delete(DELETE_QUERY, userId);
        if (result) {
            log.info("Пользователь успешно удален: userId={}", userId); // Логируем успешное удаление
            return true;
        } else {
            throw new InternalServerException("Не удалось удалить пользователя с id " + userId);
        }
    }
}