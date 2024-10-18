package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service // Аннотация указывает, что данный класс является сервисом и может быть использован в контексте Spring
public class UserService {

    private final UserStorage userStorage; // Хранение ссылки на объект UserStorage для работы с данными о пользователях
    private final FriendDbStorage friendDbStorage;

    // Конструктор, принимающий UserStorage в качестве параметра
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendDbStorage friendDbStorage) {
        this.userStorage = userStorage;
        this.friendDbStorage = friendDbStorage;
    }

    // Метод для получения всех пользователей из хранилища
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    // Метод для получения пользователя по его идентификатору
    public Optional<User> getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    // Метод для создания нового пользователя
    public User userCreate(User user) {
        return userStorage.userCreate(user);
    }

    // Метод для обновления существующего пользователя
    public User userUpdate(UpdateUserRequest request) {
        if (!request.hasId()) {
            throw new InternalServerException("Не передан id пользователя");
        }
        User updateUser = userStorage.getUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updateUser = userStorage.userUpdate(updateUser);
        return updateUser;
    }

    // Метод для добавления пользователя в друзья
    public UserDto friending(Long userId, Long friendId) {
        // Получаем пользователя по userId и преобразуем его в UserDto.
        // Если пользователь не найден, выбрасываем исключение NotFoundException.
        UserDto response = userStorage.getUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        // Проверяем, существует ли пользователь с friendId. Если нет, выбрасываем исключение NotFoundException.
        if (userStorage.getUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + friendId + " не найден");
        }
        // Получаем список друзей для пользователя userId и friendId.
        List<Friendship> userList = friendDbStorage.findAllFriends(userId);
        List<Friendship> friendList = friendDbStorage.findAllFriends(userId);
        // Проверяем, есть ли уже дружба между пользователями.
        // Если да, выбрасываем исключение InternalServerException.
        if (userList.stream().anyMatch(friend -> friend.getFriend_id().equals(friendId))) {
            throw new InternalServerException("Пользователь с id: " + userId + " уже добавлял в друзья пользователя " +
                    "с id: " + friendId);
        }
        boolean accept = false; // Флаг для статуса дружбы
        // Проверяем, добавил ли друг (friendId) пользователя (userId) в друзья.
        // Если да, устанавливаем статус дружбы как принятый (accept = true).
        if (friendList.stream().anyMatch(friend -> friend.getFriend_id().equals(userId))) {
            accept = true;
            friendDbStorage.updateFriendStatus(friendId, userId, accept); // Обновляем статус дружбы в базе данных.
        }
        // Добавляем новую дружбу в базу данных и получаем объект Friendship.
        Friendship friendship = friendDbStorage.addFriend(userId, friendId, accept);
        // Обновляем список друзей в ответе, добавляя нового друга.
        userList.add(friendship);
        response.setFriends(userList.stream()
                        .map(Friendship::getFriend_id)
                        .toList()
        );
        // Возвращаем обновленный объект UserDto с новым списком друзей.
        return response;
    }

    // Метод для удаления пользователя из друзей
    public UserDto unfriending(Long userId, Long friendId) {
        // Получаем пользователя по userId и преобразуем его в UserDto.
        // Если пользователь не найден, выбрасываем исключение NotFoundException.
        UserDto response = userStorage.getUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        // Проверяем, существует ли пользователь с friendId. Если нет, выбрасываем исключение NotFoundException.
        if (userStorage.getUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + friendId + " не найден");
        }
        // Получаем список друзей для пользователя userId и friendId.
        List<Friendship> userList = friendDbStorage.findAllFriends(userId);
        List<Friendship> friendList = friendDbStorage.findAllFriends(userId);

        // Проверяем, есть ли уже дружба между пользователями.
        // Если да, выбрасываем исключение InternalServerException.
        if (userList.stream().noneMatch(friend -> friend.getFriend_id().equals(friendId))) {
            /*throw new InternalServerException("Пользователь с id: " + userId + " не добавлял в друзья пользователя " +
                    "с id: " + friendId);*/
            return response;
        }
        // Проверяем, добавил ли друг (friendId) пользователя (userId) в друзья.
        // Если да, устанавливаем статус дружбы как принятый (accept = false).
        if (friendList.stream().anyMatch(friend -> friend.getFriend_id().equals(userId))) {
            // Обновляем статус дружбы в базе данных.
            friendDbStorage.updateFriendStatus(friendId, userId, false);
        }
        if (friendDbStorage.delete(userId, friendId)) {
            return response;
        }
        throw new InternalServerException("Не удалось удалить друга с id: " + friendId);
    }

    // Метод для получения списка друзей указанного пользователя
    public List<UserDto> getUserFriends(Long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        return friendDbStorage.findAllFriends(userId).stream()
                .map(Friendship::getFriend_id)
                .map(userStorage::getUserById)
                .map(Optional::get)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    // Метод для получения списка взаимных друзей между двумя пользователями
    public List<UserDto> listOfMutualFriends(Long userId, Long friendId) {
        return friendDbStorage.findMutualFriends(userId, friendId).stream()
                .map(Friendship::getFriend_id)
                .map(userStorage::getUserById)
                .map(Optional::get)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    // Метод для проверки существования пользователя по его идентификатору
    public User checkContainsUserId(Long userId) {
        return userStorage.checkContainsUserId(userId);
    }
}