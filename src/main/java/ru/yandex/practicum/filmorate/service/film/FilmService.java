package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenresFilm;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.director.DirectorDBStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorFilmDBStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.genres.GenresFilmDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service // Аннотация указывает, что данный класс является сервисом и может быть использован в контексте Spring
public class FilmService {

    private final FilmStorage filmStorage; // Хранение ссылки на объект FilmStorage для работы с данными о фильмах
    private final UserStorage userStorage; // Хранилище пользователей для проверки существования пользователей
    private final LikeDbStorage likeDbStorage; // Хранилище для работы с лайками
    private final GenresFilmDbStorage genresFilmDbStorage; // Хранилище для связи жанров и фильмов
    private final DirectorDBStorage directorDBStorage;
    private final DirectorFilmDBStorage directorFilmDBStorage;

    // Конструктор, принимающий FilmStorage, UserStorage и другие хранилища в качестве параметров
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       LikeDbStorage likeDbStorage, GenresFilmDbStorage genresFilmDbStorage,
                       DirectorDBStorage directorDBStorage,
                       DirectorFilmDBStorage directorFilmDBStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.genresFilmDbStorage = genresFilmDbStorage;
        this.directorDBStorage = directorDBStorage;
        this.directorFilmDBStorage = directorFilmDBStorage;
    }

    // Метод для получения всех фильмов из хранилища
    public List<FilmDto> getFilms() {
        return filmStorage.getFilms().stream()
                .map(FilmMapper::toFilmDto)
                .map(this::addGenresToFilmDto)
                .toList(); // Возвращаем список всех фильмов
    }

    // Метод для создания нового фильма
    public FilmDto createFilm(Film film) {
        Film newFilm = filmStorage.createFilm(film); // Создаем новый фильм в хранилище
        //извлечение списка id режиссеров и добавление из базы имени
        addDirectorsToFilm(newFilm);
        addGenresToGenresFilm(newFilm.getId(), newFilm.getGenres()); // Добавляем жанры фильма в таблицу
        return FilmMapper.toFilmDto(newFilm); // Возвращаем созданный фильм
    }

    public void addDirectorsToFilm(Film film) { //добавление режиссеров к фильму
        if (film.getDirectors() !=null &&!film.getDirectors().isEmpty()) {
            List<Director> directors = film.getDirectors();
            directors = directorDBStorage.getListDirector(directors);
            if (directors.isEmpty()) {
                log.error("Введен несуществующий id режиссера");
                throw new NotFoundException("Режиссер с таким id не существует");
            }
            film.setDirectors(directors);
            directorFilmDBStorage.createPost(film); //установка данных в таблицу соответствия фильм/режиссер
        }
    }

    // Метод для обновления существующего фильма
    public FilmDto updateFilm(UpdateFilmRequest request) {
        if (!request.hasId()) { // Проверяем, передан ли ID фильма в запросе
            throw new InternalServerException("Не передан id фильма"); // Если нет, выбрасываем исключение
        }
        // Получаем фильм по ID и обновляем его поля, если он существует
        Film updateFilm = FilmMapper.updateFilmFields(getFilmById(request.getId()), request);
        updateFilm = filmStorage.updateFilm(updateFilm); // Обновляем фильм в хранилище
        addDirectorsToFilm(updateFilm); //устанавливаем режиссеров
        return FilmMapper.toFilmDto(updateFilm); // Возвращаем обновленный фильм
    }

    // Метод для удаления фильма по id
    public void deleteFilm(Long filmId) {
        if (!likeDbStorage.getLikesByFilmId(filmId).isEmpty()) { // Есть ли лайки у фильма
            likeDbStorage.deleteLikeByFilmId(filmId); // Удаляем лайки к фильму
        }
        if (!genresFilmDbStorage.getGenresByFilmId(filmId).isEmpty()) { // Есть ли жанры у фильма
            genresFilmDbStorage.deleteGenresByFilmId(filmId); // Удаляем жанры фильма
        }
        //добавить удаление из базы фильм/режиссер
        filmStorage.deleteFilm(filmId); // Удаляем фильм
    }

    // Метод для добавления лайка к фильму от пользователя
    public FilmDto addLike(Long filmId, Long userId) {
        validateUserExists(userId);
        Film film = getFilmById(filmId);
        Like like = likeDbStorage.addLikeToFilm(filmId, userId); // Добавляем лайк к фильму
        FilmDto response = FilmMapper.toFilmDto(film); // Преобразуем фильм в DTO-объект для ответа
        response.setLikes(Set.of(like.getUserId())); // Устанавливаем набор лайков в ответе
        return response; // Возвращаем ответ с информацией о фильме и лайках
    }

    // Метод для удаления лайка от пользователя к фильму
    public FilmDto deleteLike(Long filmId, Long userId) {
        validateUserExists(userId);
        Film film = getFilmById(filmId);
        likeDbStorage.deleteLike(filmId, userId); // Удаляем лайк от пользователя к фильму
        return FilmMapper.toFilmDto(film); // Возвращаем DTO-объект фильма после удаления лайка
    }

    // Метод для получения самых популярных фильмов по количеству лайков
    public List<FilmDto> getMostPopularByNumberOfLikes(Long count) {
        // Возвращаем список самых популярных фильмов по количеству лайков
        return filmStorage.getMostPopularByNumberOfLikes(count).stream()
                .map(FilmMapper::toFilmDto)
                .map(this::addGenresToFilmDto)
                .toList();
    }

    // Метод для получения фильма с его жанрами по идентификатору
    public FilmDto getWithGenre(Long id) {
        // Получаем фильм по ID, и преобразовываем в FilmDto
        FilmDto filmDto = FilmMapper.toFilmDto(getFilmById(id));
        // Добавляем список жанров к фильму
        addGenresToFilmDto(filmDto);
        //поиск режиссеров по id фильма
        List<Director> directors = directorFilmDBStorage.getDirectorsForFilm(id);
        filmDto.setDirectors(directors);
        return filmDto; // Возвращаем фильм с установленными жанрами
    }

    // Метод для добавления жанров в таблицу с жанрами фильма
    private void addGenresToGenresFilm(Long filmId, List<Genre> genresList) {
        if (genresList != null) {
            // Если жанры существуют, добавляем их в хранилище связей жанров и фильмов
            Set<Genre> uniqueGenres = new LinkedHashSet<>(genresList);
            uniqueGenres.forEach(genre -> genresFilmDbStorage.addGenreToFilm(filmId, genre.getId()));
        }
    }

    private void validateUserExists(Long userId) {
        userStorage.getUserById(userId) // Проверяем, существует ли пользователь с данным идентификатором
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
    }

    // Метод для получения фильма по его идентификатору
    private Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId) // Возвращаем фильм
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
    }

    // Метод для добавления жанров к фильму
    private FilmDto addGenresToFilmDto(FilmDto filmDto) {
        List<Genre> filmGenresList = genresFilmDbStorage.getGenresByFilmId(filmDto.getId()).stream()
                .map(GenresFilm::getGenre)
                .toList();
        filmDto.setGenres(filmGenresList); // Устанавливаем полученные жанры для фильма
        return filmDto;
    }
}