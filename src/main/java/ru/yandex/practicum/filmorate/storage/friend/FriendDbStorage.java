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

    private static final String INSERT_FRIEND = "INSERT INTO FRIENDSHIP(user_id, friend_id, accept) VALUES (?, ?, ?)";
    private static final String FIND_FRIEND_BY_ID = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String FIND_ALL_FRIEND = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ?";
    private static final String UPDATE_FRIEND_BY_ID = "UPDATE FRIENDSHIP SET ACCEPT = ? " +
            "WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String DELETE_FRIEND = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String FIND_MUTUAL_FRIENDS = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID " +
            "IN (SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?)";

    public FriendDbStorage(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper, Friendship.class);
    }

    public Friendship addFriend(Long userId, Long friendId, boolean accept) {
        Friendship friendship = Friendship.builder()
                .user_id(userId)
                .friend_id(friendId)
                .accept(accept)
                .build();
        int id = jdbc.update(
                INSERT_FRIEND,
                userId,
                friendId,
                accept
        );
        return friendship;
    }

    public List<Friendship> findAllFriends(Long userId) {
        return findMany(FIND_ALL_FRIEND, userId);
    }

    public Optional<Friendship> findFriendById(Long userId, Long friendId) {
        return findOne(FIND_FRIEND_BY_ID, userId, friendId);
    }

    public boolean delete(Long userId, Long friendId) {
        return delete(DELETE_FRIEND, userId, friendId);
    }

    public Friendship updateFriendStatus(Long userId, Long friendId, boolean accept) {
        Friendship friendship = Friendship.builder()
                .user_id(userId)
                .friend_id(friendId)
                .accept(accept)
                .build();
        update(
                UPDATE_FRIEND_BY_ID,
                accept,
                userId,
                friendId
        );
        return friendship;
    }

    public List<Friendship> findMutualFriends(Long userId, Long friendId) {
        return findMany(FIND_MUTUAL_FRIENDS, userId, friendId);
    }
}
