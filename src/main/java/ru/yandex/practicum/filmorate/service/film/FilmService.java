package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenresFilm;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.genres.GenresFilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Service // Аннотация указывает, что данный класс является сервисом и может быть использован в контексте Spring
public class FilmService {

    private final FilmStorage filmStorage; // Хранение ссылки на объект FilmStorage для работы с данными о фильмах
    private final UserStorage userStorage; // Хранилище пользователей для проверки существования пользователей
    private final GenreDbStorage genreDbStorage; // Хранилище для работы с жанрами
    private final LikeDbStorage likeDbStorage; // Хранилище для работы с лайками
    private final GenresFilmDbStorage genresFilmDbStorage; // Хранилище для связи жанров и фильмов

    // Конструктор, принимающий FilmStorage, UserStorage и другие хранилища в качестве параметров
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       GenreDbStorage genreDbStorage, LikeDbStorage likeDbStorage,
                       GenresFilmDbStorage genresFilmDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.genresFilmDbStorage = genresFilmDbStorage;
    }

    // Метод для получения всех фильмов из хранилища
    public List<FilmDto> getFilms() {
        return filmStorage.getFilms().stream().map(FilmMapper::toFilmDto).toList(); // Возвращаем список всех фильмов
    }

    // Метод для получения фильма по его идентификатору
    public FilmDto getFilmById(Long filmId) {
        return FilmMapper.toFilmDto(filmStorage.getFilmById(filmId) // Возвращаем фильм
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден")));
    }

    // Метод для создания нового фильма
    public FilmDto createFilm(Film film) {
        Film newFilm = filmStorage.createFilm(film); // Создаем новый фильм в хранилище
        List<Genre> genresList = film.getGenres(); // Получаем список жанров фильма
        if (genresList != null) {
            // Если жанры существуют, добавляем их в хранилище связей жанров и фильмов
            film.getGenres().forEach(genre -> genresFilmDbStorage.addGenreToFilm(newFilm.getId(), genre.getId()));
        }
        return FilmMapper.toFilmDto(newFilm); // Возвращаем созданный фильм
    }

    // Метод для обновления существующего фильма
    public FilmDto updateFilm(UpdateFilmRequest request) {
        if (!request.hasId()) { // Проверяем, передан ли ID фильма в запросе
            throw new InternalServerException("Не передан id фильма"); // Если нет, выбрасываем исключение
        }
        // Получаем фильм по ID и обновляем его поля, если он существует
        Film updateFilm = filmStorage.getFilmById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                // Если фильм не найден, выбрасываем исключение
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        updateFilm = filmStorage.updateFilm(updateFilm); // Обновляем фильм в хранилище
        return FilmMapper.toFilmDto(updateFilm); // Возвращаем обновленный фильм
    }

    // Метод для добавления лайка к фильму от пользователя
    public FilmDto addLike(Long filmId, Long userId) {
        userStorage.getUserById(userId) // Проверяем, существует ли пользователь с данным идентификатором
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        Film film = filmStorage.getFilmById(filmId)
                // Проверяем, существует ли фильм с данным идентификатором
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + userId + " не найден"));
        Like like = likeDbStorage.addLikeToFilm(filmId, userId); // Добавляем лайк к фильму
        FilmDto response = FilmMapper.toFilmDto(film); // Преобразуем фильм в DTO-объект для ответа
        response.setLikes(Set.of(like.getUserId())); // Устанавливаем набор лайков в ответе
        return response; // Возвращаем ответ с информацией о фильме и лайках
    }

    // Метод для удаления лайка от пользователя к фильму
    public FilmDto deleteLike(Long filmId, Long userId) {
        userStorage.getUserById(userId) // Проверяем, существует ли пользователь с данным идентификатором
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        Film film = filmStorage.getFilmById(filmId)
                // Проверяем, существует ли фильм с данным идентификатором
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + userId + " не найден"));
        likeDbStorage.deleteLike(filmId, userId); // Удаляем лайк от пользователя к фильму
        return FilmMapper.toFilmDto(film); // Возвращаем DTO-объект фильма после удаления лайка
    }

    // Метод для получения самых популярных фильмов по количеству лайков
    public List<FilmDto> getMostPopularByNumberOfLikes(Long count) {
        // Возвращаем список самых популярных фильмов по количеству лайков
        return filmStorage.getMostPopularByNumberOfLikes(count).stream().map(FilmMapper::toFilmDto).toList();
    }

    // Метод для получения фильма с его жанрами по идентификатору
    public FilmDto getWithGenre(Long id) {
        // Получаем фильм по ID, если фильм не найден, выбрасываем исключение NotFoundException
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + id + " не найден"));
        // Получаем список идентификаторов жанров, связанных с данным фильмом
        List<Long> genreIdList = genresFilmDbStorage.getGenresByFilmId(id).stream()
                // Извлекаем идентификаторы жанров из объектов GenresFilm
                .map(GenresFilm::getGenreId)
                .toList(); // Преобразуем в список
        // Получаем список всех доступных жанров
        List<Genre> genresList = genreDbStorage.getAllGenres();
        // Фильтруем список жанров, оставляя только те, которые соответствуют идентификаторам из genreIdList
        List<Genre> filmGenresList = genresList.stream()
                // Проверяем наличие идентификатора жанра в списке
                .filter(genre -> genreIdList.contains(genre.getId()))
                .toList(); // Преобразуем в список
        film.setGenres(filmGenresList); // Устанавливаем полученные жанры для фильма
        return FilmMapper.toFilmDto(film); // Возвращаем фильм с установленными жанрами
    }
}