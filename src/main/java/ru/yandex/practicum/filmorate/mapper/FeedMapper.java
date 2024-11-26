package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.feed.FeedDto;
import ru.yandex.practicum.filmorate.model.Feed;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedMapper {
    public static FeedDto mapToFeedDto(Feed feed) {
        return FeedDto.builder()
                .eventId(feed.getEventId())
                .timestamp(feed.getTimestamp())
                .entityId(feed.getEntityId())
                .eventType(feed.getEventType())
                .operation(feed.getOperation())
                .build();
    }
}
