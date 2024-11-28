package ru.yandex.practicum.filmorate.eventHanding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.ArrayList;
import java.util.List;

@Component
public class FeedEventSource { //класс, управляющий слушателями
    private final List<FeedListeners> listeners = new ArrayList<>(); //список слушателей(обработчиков)

    @Autowired
    public FeedEventSource(FeedNotificationService feedNotificationService) {
        this.listeners.add(feedNotificationService); //зарегистрировали слушателя
    }

        public void notifyFeedListeners(Feed feed) { //метод оповещения слушателей, отправляем событие на обработку
        for (FeedListeners listener: listeners) { //цикл на случай, если слушателей(обработчиков) несколько
            listener.feedAdded(feed);
        }
    }
}
