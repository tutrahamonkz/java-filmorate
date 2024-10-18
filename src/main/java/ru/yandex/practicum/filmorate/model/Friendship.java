package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Friendship {
    @NonNull
    private Long user_id;
    @NonNull
    private Long friend_id;
    private boolean accept;
}
