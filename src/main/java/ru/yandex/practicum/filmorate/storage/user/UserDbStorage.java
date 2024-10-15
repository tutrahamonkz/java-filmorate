package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(login, email, name, friends, birthday)" +
            "VALUES (?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?," +
            " WHERE id = ?";

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
                user.getFriends(),
                Timestamp.from(Instant.from(user.getBirthday()))
        );
        user.setId(id);
        return user;
    }

    @Override
    public User userUpdate(User user) {
        update(UPDATE_QUERY,
                user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getFriends(),
                Timestamp.from(Instant.from(user.getBirthday()))
        );
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
