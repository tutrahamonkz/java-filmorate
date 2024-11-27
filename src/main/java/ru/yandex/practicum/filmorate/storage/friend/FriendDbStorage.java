package ru.yandex.practicum.filmorate.storage.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FriendDbStorage extends BaseStorage<Friendship> {
    // SQL-запросы для работы с таблицей FRIENDSHIP
    private static final String INSERT_FRIEND = "INSERT INTO FRIENDSHIP(user_id, friend_id, accept) VALUES (?, ?, ?)";
    private static final String FIND_FRIEND_BY_ID = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String FIND_ALL_FRIEND = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ?";
    private static final String UPDATE_FRIEND_BY_ID = "UPDATE FRIENDSHIP SET ACCEPT = ? " +
            "WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String DELETE_FRIEND = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String DELETE_FRIENDS_BY_USER_ID = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? OR FRIEND_ID = ?";
    private static final String FIND_MUTUAL_FRIENDS = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID " +
            "IN (SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?)";

    public FriendDbStorage(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper, Friendship.class);
    }

    // Метод для добавления нового друга
    public Friendship addFriend(Long userId, Long friendId, boolean accept) {
        // Логируем добавление друга
        log.info("Добавление друга: userId={}, friendId={}, accept={}", userId, friendId, accept);
        Friendship friendship = Friendship.builder() // Создаем объект Friendship с указанными параметрами
                .userId(userId)
                .friendId(friendId)
                .accept(accept)
                .build();
        // Выполняем SQL-запрос на добавление друга
        /*int id = jdbc.update(
                INSERT_FRIEND,
                userId,
                friendId,
                accept
        );*/
        //новый код
        Long friendshipId = insert(INSERT_FRIEND,
                userId,
                friendId,
                accept);
        friendship.setId(friendshipId);
        //новый код
        log.info("Друг успешно добавлен: {}", friendship); // Логируем успешное добавление
        return friendship; // Возвращаем объект дружбы
    }

    // Метод для получения всех друзей пользователя
    public List<Friendship> findAllFriends(Long userId) {
        log.info("Запрос всех друзей для userId={}", userId); // Логируем запрос на получение всех друзей
        List<Friendship> friends = findMany(FIND_ALL_FRIEND, userId); // Получаем список друзей из БД
        log.info("Найдено {} друзей для userId={}", friends.size(), userId); // Логируем количество найденных друзей
        return friends; // Возвращаем список друзей
    }

    // Метод для поиска конкретного друга по ID пользователя и ID друга
    public Optional<Friendship> findFriendById(Long userId, Long friendId) {
        log.info("Запрос друга по ID: userId={}, friendId={}", userId, friendId); // Логируем запрос друга по ID
        return findOne(FIND_FRIEND_BY_ID, userId, friendId); // Ищем друга в базе данных и возвращаем результат
    }

    // Метод для удаления друга из списка друзей
    public boolean delete(Long userId, Long friendId) {
        log.info("Удаление друга: userId={}, friendId={}", userId, friendId); // Логируем удаление друга
        // Выполняем SQL-запрос на удаление записи о дружбе из БД
        boolean result = delete(DELETE_FRIEND, userId, friendId);
        if (result) {
            log.info("Друг успешно удален: userId={}, friendId={}", userId, friendId); // Логируем успешное удаление
        }
        return result; // Возвращаем результат операции удаления
    }

    // Метод для удаления друзей по id пользователя
    public boolean deleteFriendsByUserId(Long userId) {
        log.info("Удаление друзей для userId={}", userId); // Логируем удаление друзей
        // Выполняем SQL-запрос на удаление записей о дружбе из БД
        boolean result = delete(DELETE_FRIENDS_BY_USER_ID, userId, userId);
        if (result) {
            log.info("Записи о дружбе пользователя: id={}, успешно удалены", userId); // Логируем успешное удаление
            return true;
        }
        return false;
    }

    // Метод для обновления статуса дружбы (принят/не принят)
    public Friendship updateFriendStatus(Long userId, Long friendId, boolean accept) {
        // Логируем обновление статуса дружбы
        log.info("Обновление статуса дружбы: userId={}, friendId={}, accept={}", userId, friendId, accept);
        // Создаем объект Friendship с обновленным статусом
        Friendship friendship = Friendship.builder()
                .userId(userId)
                .friendId(friendId)
                .accept(accept)
                .build();
        update(UPDATE_FRIEND_BY_ID, // Выполняем SQL-запрос на обновление статуса дружбы в БД
                accept,
                userId,
                friendId
        );
        log.info("Статус дружбы успешно обновлен: {}", friendship); // Логируем успешное обновление статуса
        return friendship; // Возвращаем объект с обновленным статусом
    }

    public List<Friendship> findMutualFriends(Long userId, Long friendId) {
        // Логируем запрос на получение взаимных друзей
        log.info("Запрос взаимных друзей: userId={}, friendId={}", userId, friendId);
        // Выполняем SQL-запрос для поиска взаимных друзей
        List<Friendship> mutualFriends = findMany(FIND_MUTUAL_FRIENDS, userId, friendId);
        // Логируем количество найденных взаимных друзей
        log.info("Найдено {} взаимных друзей между userId={} и friendId={}", mutualFriends.size(), userId, friendId);
        return mutualFriends;  // Возвращаем список взаимных друзей
    }
}