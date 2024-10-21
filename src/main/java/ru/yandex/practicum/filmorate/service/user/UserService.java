package ru.yandex.practicum.filmorate.service.user;

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

import java.util.List;
import java.util.Optional;

@Service // Аннотация указывает, что данный класс является сервисом и может быть использован в контексте Spring
public class UserService {

    private final UserStorage userStorage; // Хранение ссылки на объект UserStorage для работы с данными о пользователях
    // Хранение ссылки на объект FriendDbStorage для работы с дружескими отношениями
    private final FriendDbStorage friendDbStorage;

    // Конструктор, принимающий UserStorage в качестве параметра
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendDbStorage friendDbStorage) {
        this.userStorage = userStorage;
        this.friendDbStorage = friendDbStorage;
    }

    // Метод для получения всех пользователей из хранилища
    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    // Метод для получения пользователя по его идентификатору
    public UserDto getUserById(Long id) {
        // Получаем пользователя по id и преобразуем в UserDto
        UserDto user = UserMapper.mapToUserDto(userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден")));
        // Получаем друзей пользователя
        List<Friendship> friendships = friendDbStorage.findAllFriends(id);
        // Добавляем друзей пользователю
        user.setFriends(friendships.stream().map(Friendship::getFriendId).toList());
        return user; // Возвращаем пользователя с друзьями
    }

    // Метод для создания нового пользователя
    public UserDto userCreate(User user) {
        // Создаем пользователя и преобразуем в UserDto
        return UserMapper.mapToUserDto(userStorage.userCreate(user));
    }

    // Метод для обновления существующего пользователя
    public UserDto userUpdate(UpdateUserRequest request) {
        if (!request.hasId()) { // Проверяем, был ли передан id пользователя
            // Выбрасываем исключение, если id отсутствует
            throw new NotFoundException("Не передан id пользователя");
        }
        User updateUser = userStorage.getUserById(request.getId()) // Получаем пользователя по id из запроса
                // Обновляем поля пользователя на основе данных из запроса
                .map(user -> UserMapper.updateUserFields(user, request))
                // Выбрасываем исключение, если пользователь не найден
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updateUser = userStorage.userUpdate(updateUser); // Обновляем пользователя в хранилище
        return UserMapper.mapToUserDto(updateUser); // Возвращаем обновленного пользователя в формате UserDto
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
        if (userList.stream().anyMatch(friend -> friend.getFriendId().equals(friendId))) {
            throw new InternalServerException("Пользователь с id: " + userId + " уже добавлял в друзья пользователя " +
                    "с id: " + friendId);
        }
        boolean accept = false; // Флаг для статуса дружбы
        // Проверяем, добавил ли друг (friendId) пользователя (userId) в друзья.
        // Если да, устанавливаем статус дружбы как принятый (accept = true).
        if (friendList.stream().anyMatch(friend -> friend.getFriendId().equals(userId))) {
            accept = true;
            friendDbStorage.updateFriendStatus(friendId, userId, accept); // Обновляем статус дружбы в базе данных.
        }
        // Добавляем новую дружбу в базу данных и получаем объект Friendship.
        Friendship friendship = friendDbStorage.addFriend(userId, friendId, accept);
        // Обновляем список друзей в ответе, добавляя нового друга.
        userList.add(friendship);
        response.setFriends(userList.stream()
                        .map(Friendship::getFriendId)
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
        if (userList.stream().noneMatch(friend -> friend.getFriendId().equals(friendId))) {
            /*throw new InternalServerException("Пользователь с id: " + userId + " не добавлял в друзья пользователя " +
                    "с id: " + friendId);*/
            return response;
        }
        // Проверяем, добавил ли друг (friendId) пользователя (userId) в друзья.
        // Если да, устанавливаем статус дружбы как принятый (accept = false).
        if (friendList.stream().anyMatch(friend -> friend.getFriendId().equals(userId))) {
            // Обновляем статус дружбы в базе данных.
            friendDbStorage.updateFriendStatus(friendId, userId, false);
        }
        // Удаляем дружбу из базы данных и проверяем результат операции.
        if (friendDbStorage.delete(userId, friendId)) {
            return response;
        }
        // Если удаление не удалось, выбрасываем исключение InternalServerException.
        throw new InternalServerException("Не удалось удалить друга с id: " + friendId);
    }

    // Метод для получения списка друзей указанного пользователя
    public List<UserDto> getUserFriends(Long userId) {
        // Проверяем, существует ли пользователь с указанным userId. Если нет, выбрасываем исключение NotFoundException.
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        // Получаем список друзей пользователя и преобразуем их в UserDto.
        return friendDbStorage.findAllFriends(userId).stream()
                .map(Friendship::getFriendId)
                .map(userStorage::getUserById)
                .map(Optional::get)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    // Метод для получения списка взаимных друзей между двумя пользователями
    public List<UserDto> listOfMutualFriends(Long userId, Long friendId) {
        // Получаем список взаимных друзей и преобразуем их в UserDto.
        return friendDbStorage.findMutualFriends(userId, friendId).stream()
                .map(Friendship::getFriendId)
                .map(userStorage::getUserById)
                .map(Optional::get)
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}