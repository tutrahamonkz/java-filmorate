package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserDbStorage extends BaseStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO USERS(login, email, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET login = ?, email = ?, name = ?, birthday = ?" +
            " WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }
    @Override
    public List<User> getUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    @Override
    public User userCreate(User user) {
        Long id = insert(
                INSERT_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay())
        );
        user.setId(id);
        log.info("Создан пользователь с id: {}", id); // Логируем информацию о создании пользователя
        return user;
    }

    @Override
    public User userUpdate(User user) {
        update(
                UPDATE_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay()),
                user.getId()
        );
        log.info("Обновлен пользователь с id: {}", user.getId()); // Логируем информацию об обновлении
        return user;
    }

    @Override
    public User friending(Long userId, Long friendId) {
        return null;
    }

    @Override
    public User unfriending(Long userId, Long friendId) {
        return null;
    }

    @Override
    public List<User> getUserFriends(Long userId) {
        return List.of();
    }

    @Override
    public List<User> listOfMutualFriends(Long userId, Long friendId) {
        return List.of();
    }

    @Override
    public User checkContainsUserId(Long userId) {
        return null;
    }
}
