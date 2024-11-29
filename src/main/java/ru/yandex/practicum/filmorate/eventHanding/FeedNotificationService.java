package ru.yandex.practicum.filmorate.eventHanding;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.event.FeedDbStorage;

@Component
@RequiredArgsConstructor
public class FeedNotificationService implements FeedListeners { //реализация слушателя

    private final FeedDbStorage feedDbStorage;

    @Override
    public void feedAdded(Feed feed) { //обработка события - добавление записи в таблицу
        feedDbStorage.createFeed(feed);
    }
}
