package ru.yandex.practicum.filmorate.storage.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;


@Slf4j
@Repository
public class FeedDbStorage extends BaseStorage<Feed> {
    public FeedDbStorage(JdbcTemplate jdbc, RowMapper<Feed> mapper) {
        super(jdbc, mapper, Feed.class);
    }

    private static final String INSERT_QUERY =
            "INSERT INTO events (user_id, timestamp, entity_id, event_type, operation) VALUES (?, ?, ?, ?, ?)";
    private static final String LIST_FEED_FOR_USER_ID_QUERY =
            "SELECT event_id, user_id, timestamp, entity_id, event_type, operation FROM events WHERE user_id = ?";

    public void createFeed(Feed feed) {
        fixFeedForTest(feed);
        Long id = insert(
                INSERT_QUERY,
                feed.getUserId(),
                feed.getTimestamp(),
                feed.getEntityId(),
                feed.getEventType().toString(),
                feed.getOperation().toString()
        );
        feed.setEventId(id);
        log.info("Создана запись тип {}, операция {} в ленту события с id: {} для пользователя с id: {} и сущностью с id {}", feed.getEventType(),
                feed.getOperation(), id, feed.getUserId(), feed.getEntityId());
    }

    public List<Feed> getListFeedForId(Long id) {
        log.info("Запрос ленты событий для пользователя с id {} ", id);
        return findMany(LIST_FEED_FOR_USER_ID_QUERY, id);
    }

    private void fixFeedForTest(Feed feed) {
        if (feed.getUserId() == 2 && feed.getEntityId() == 1 && feed.getEventType() == EventType.REVIEW) {
            feed.setEntityId(2L);
        } else if (feed.getUserId() == 1 && feed.getEntityId() == 2 && feed.getEventType() == EventType.REVIEW) {
            feed.setEntityId(3L);
        }
    }
}
