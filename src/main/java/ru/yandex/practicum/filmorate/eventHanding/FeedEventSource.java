package ru.yandex.practicum.filmorate.eventHanding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class FeedEventSource { //класс, управляющий слушателями
    private final List<FeedListeners> listeners = new ArrayList<>(); //список слушателей(обработчиков)

    @Autowired
    public FeedEventSource(FeedNotificationService feedNotificationService) {
        this.listeners.add(feedNotificationService); //зарегистрировали слушателя
    }

    public void notifyFeedListeners(Long userId, //метод оповещения слушателей, отправляем событие на обработку
                                    Long entityId,
                                    EventType eventType,
                                    Operation operation) {
        Feed feed = Feed.builder()
                .userId(userId)
                .timestamp(Timestamp.from(Instant.now()))
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .build();

        for (FeedListeners listener : listeners) { //цикл на случай, если слушателей(обработчиков) несколько
            listener.feedAdded(feed);
        }
    }
}
