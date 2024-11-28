package ru.yandex.practicum.filmorate.service.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.feed.FeedDto;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.event.FeedDbStorage;

import java.util.List;
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedDbStorage feedDbStorage;
    private final UserService userService;
    public List<FeedDto> getUserFeed(Long id) {
        userService.findUserById(id);
        return feedDbStorage.getListFeedForId(id).stream()
                .map(FeedMapper::mapToFeedDto)
                .toList();
    }
}
