package ru.yandex.practicum.filmorate.eventHanding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.ArrayList;
import java.util.List;

@Component
public class FeedEventSource { //класс, управляющий слушателями и генерирующий события
    private List<FeedListeners> listeners = new ArrayList<>();

    @Autowired
    public FeedEventSource(FeedNotificationService feedNotificationService) {
        this.listeners.add(feedNotificationService); //зарегистрировали слушателя
    }

    //на случай добавления еще слушателей
    //метод позволяет добавлять слушателей в список. Когда вы хотите, чтобы какой-то объект
    // реагировал на событие добавления друга, вы вызываете этот метод и передаете объект,
    // который реализует интерфейс FeedListeners
    public void addListener(FeedListeners listener) {
        listeners.add(listener);
    }
    //метод позволяет удалять слушателей из списка
    public void removeListener(FeedListeners listener) {
        listeners.remove(listener);
    }
        public void notifyFeedListeners(Feed feed) { //отправили слушателям событие на обработку
        for (FeedListeners listener: listeners) { //цикл на случай, если слушателей(обработчиков) несколько
            listener.feedAdded(feed);
        }
    }
}
