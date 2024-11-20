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
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service // Аннотация указывает, что данный класс является сервисом и может быть использован в контексте Spring
public class UserService {

    private final UserStorage userStorage; // Хранение ссылки на объект UserStorage для работы с данными о пользователях
    // Хранение ссылки на объект FriendDbStorage для работы с дружескими отношениями
    private final FriendDbStorage friendDbStorage;
    private final LikeDbStorage likeDbStorage;

    // Конструктор, принимающий UserStorage в качестве параметра
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendDbStorage friendDbStorage,
                       LikeDbStorage likeDbStorage) {
        this.userStorage = userStorage;
        this.friendDbStorage = friendDbStorage;
        this.likeDbStorage = likeDbStorage;
    }

    // Метод для получения всех пользователей из хранилища
    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    // Метод для получения пользователя по его идентификатору
    public UserDto getUserById(Long id) {
        User user = findUserById(id); // Получаем пользователя по id
        return createUserDtoWithFriends(user); // Возвращаем пользователя с друзьями
    }

    // Метод для создания нового пользователя
    public UserDto userCreate(User user) {
        // Создаем пользователя и преобразуем в UserDto
        return UserMapper.mapToUserDto(userStorage.userCreate(user));
    }

    // Метод для обновления существующего пользователя
    public UserDto userUpdate(UpdateUserRequest request) {
        validateRequest(request);
        User user = findUserById(request.getId()); // Получаем пользователя по id из запроса
        User updateUser = UserMapper.updateUserFields(user, request);
        updateUser = userStorage.userUpdate(updateUser); // Обновляем пользователя в хранилище
        return UserMapper.mapToUserDto(updateUser); // Возвращаем обновленного пользователя в формате UserDto
    }

    // Метод для добавления пользователя в друзья
    public UserDto friending(Long userId, Long friendId) {
        // Получаем пользователя по userId и преобразуем его в UserDto.
        UserDto response = UserMapper.mapToUserDto(findUserById(userId));
        findUserById(friendId); // Проверяем, существует ли пользователь с friendId.
        // Получаем список друзей для пользователя userId и friendId.
        List<Friendship> userList = friendDbStorage.findAllFriends(userId);
        List<Friendship> friendList = friendDbStorage.findAllFriends(userId);
        // Проверяем, есть ли уже дружба между пользователями.
        // Если да, выбрасываем исключение InternalServerException.
        if (checkFriendshipExists(userList, userId, friendId)) {
            throw new InternalServerException("Пользователь с id: " + userId + " уже добавлял пользователя с id: "
                    + friendId + " в друзья");
        }
        boolean accept = checkAndUpdateFriendshipStatus(userId, friendId, friendList, true);
        // Добавляем новую дружбу в базу данных и получаем объект Friendship.
        Friendship friendship = friendDbStorage.addFriend(userId, friendId, accept);
        // Обновляем список друзей в ответе, добавляя нового друга.
        userList.add(friendship);
        response.setFriends(userList.stream()
                        .map(Friendship::getFriendId)
                        .toList()
        );
        return response; // Возвращаем обновленный объект UserDto с новым списком друзей.
    }

    // Метод для удаления пользователя из друзей
    public UserDto unfriending(Long userId, Long friendId) {
        // Получаем пользователя по userId и преобразуем его в UserDto.
        UserDto response = UserMapper.mapToUserDto(findUserById(userId));
        findUserById(friendId); // Проверяем, существует ли пользователь с friendId.
        // Получаем список друзей для пользователя userId и friendId.
        List<Friendship> userList = friendDbStorage.findAllFriends(userId);
        List<Friendship> friendList = friendDbStorage.findAllFriends(userId);
        // Проверяем, есть ли уже дружба между пользователями.
        if (!checkFriendshipExists(userList, userId, friendId)) {
            return response;
        }
        // Проверяем, добавил ли друг (friendId) пользователя (userId) в друзья.
        // Если да, устанавливаем статус дружбы (accept = false).
        checkAndUpdateFriendshipStatus(userId, friendId, friendList, false);
        // Удаляем дружбу из базы данных и проверяем результат операции.
        if (friendDbStorage.delete(userId, friendId)) {
            return response;
        }
        // Если удаление не удалось, выбрасываем исключение InternalServerException.
        throw new InternalServerException("Не удалось удалить друга с id: " + friendId);
    }

    // Метод для получения списка друзей указанного пользователя
    public List<UserDto> getUserFriends(Long userId) {
        // Проверяем, существует ли пользователь с указанным userId.
        findUserById(userId);
        // Получаем список друзей пользователя и преобразуем их в UserDto.
        return listFriendshipToListUserDto(friendDbStorage.findAllFriends(userId));
    }

    // Метод для получения списка взаимных друзей между двумя пользователями
    public List<UserDto> listOfMutualFriends(Long userId, Long friendId) {
        // Получаем список взаимных друзей и преобразуем их в UserDto.
        return listFriendshipToListUserDto(friendDbStorage.findMutualFriends(userId, friendId));
    }

    private User findUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
    }

    private UserDto createUserDtoWithFriends(User user) {
        // Получаем друзей пользователя
        List<Friendship> friendships = friendDbStorage.findAllFriends(user.getId());
        UserDto userDto = UserMapper.mapToUserDto(user);
        // Добавляем друзей пользователю
        userDto.setFriends(friendships.stream().map(Friendship::getFriendId).toList());
        return userDto;
    }

    private void validateRequest(UpdateUserRequest request) {
        if (!request.hasId()) { // Проверяем, был ли передан id пользователя
            // Выбрасываем исключение, если id отсутствует
            throw new NotFoundException("Не передан id пользователя");
        }
    }

    private boolean checkFriendshipExists(List<Friendship> userFriends, Long userId, Long friendId) {
        return userFriends.stream().anyMatch(friend -> friend.getFriendId().equals(friendId));
    }

    private boolean checkAndUpdateFriendshipStatus(Long userId, Long friendId, List<Friendship> friendFriends,
                                                   boolean accept) {
        // Проверяем, добавил ли друг (friendId) пользователя (userId) в друзья.
        // Если да, устанавливаем нужный статус дружбы
        if (friendFriends.stream().anyMatch(friend -> friend.getFriendId().equals(userId))) {
            friendDbStorage.updateFriendStatus(friendId, userId, accept); // Обновляем статус дружбы в базе данных.
        } else {
            accept = !accept;
        }
        return accept;
    }

    private List<UserDto> listFriendshipToListUserDto(List<Friendship> list) {
        return list.stream()
                .map(Friendship::getFriendId)
                .map(userStorage::getUserById)
                .flatMap(Optional::stream)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    // Метод для удаления пользователя
    public void deleteUser(Long userId) {
        try {
            friendDbStorage.deleteFriendsByUserId(userId); // Удаляем записи о пользователе из друзей
        } catch (InternalServerException ignore) {}
        try {
            likeDbStorage.deleteLikeByUserId(userId); // Удаляем записи о пользователе из лайков
        } catch (InternalServerException ignore) {}
        userStorage.deleteUser(userId); // Удаляем пользователя
    }
}