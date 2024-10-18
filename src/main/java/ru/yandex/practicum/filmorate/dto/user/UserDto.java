package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private List<Long> friends;

    public void setFriend(Long id) {
        friends.add(id);
    }
}