package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public class UserDbStorage implements UserStorage {
    @Override
    public Collection<User> getUsers() {
        return List.of();
    }

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public User userCreate(User user) {
        return null;
    }

    @Override
    public User userUpdate(User user) {
        return null;
    }

    @Override
    public User friending(Long userId, Long friendId) {
        return null;
    }

    @Override
    public User unfriending(Long userId, Long friendId) {
        return null;
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        return List.of();
    }

    @Override
    public Collection<User> listOfMutualFriends(Long userId, Long friendId) {
        return List.of();
    }

    @Override
    public User checkContainsUserId(Long userId) {
        return null;
    }
}
