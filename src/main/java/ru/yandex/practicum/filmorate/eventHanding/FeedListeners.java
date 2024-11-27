package ru.yandex.practicum.filmorate.eventHanding;

import ru.yandex.practicum.filmorate.model.Feed;

//интерфейс слушателей, которые обрабатывают события лайки,отзывы, дружбы
public interface FeedListeners {
    void feedAdded(Feed feed); //метод, который обрабатывает события
}
