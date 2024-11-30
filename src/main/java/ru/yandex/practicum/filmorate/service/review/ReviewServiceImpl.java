package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.eventHanding.FeedEventSource;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewDbRepository;
    private final FilmDbStorage filmDbRepository;
    private final UserDbStorage userDbRepository;
    private final FeedEventSource feedEventSource;

    @Override
    public Review addReview(Review review) {
        log.info("Добавление отзыва от пользователя с id: {} к фильму с id: {}",
                review.getUserId(), review.getFilmId());
        checkFilmExist(review.getFilmId());
        checkUserExist(review.getUserId());
        Review addedReview = reviewDbRepository.addReview(review);

        feedEventSource.notifyFeedListeners(
                addedReview.getUserId(),
                addedReview.getReviewId(),
                EventType.REVIEW,
                Operation.ADD);

        log.info("Успешно добавлен отзыв с id: {}, от пользователя с id: {} к фильму с id: {}",
                review.getReviewId(), review.getUserId(), review.getFilmId());
        return addedReview;
    }

    @Override
    public Review updateReview(Review review) {
        log.info("Изменение отзыва с id: {} от пользователя с id: {} к фильму с id: {}",
                review.getReviewId(), review.getUserId(), review.getFilmId());

        checkFilmExist(review.getFilmId());
        checkUserExist(review.getUserId());
        checkReviewExist(review.getReviewId());
        Review updatedReview = reviewDbRepository.updateReview(review);

        feedEventSource.notifyFeedListeners(
                updatedReview.getUserId(),
                updatedReview.getReviewId(),
                EventType.REVIEW,
                Operation.UPDATE);

        log.info("Успешно изменен отзыв с id: {}, от пользователя с id: {} к фильму с id: {}",
                review.getReviewId(), review.getUserId(), review.getFilmId());
        return updatedReview;
    }

    @Override
    public Boolean deleteReview(Long id) {
        log.info("Удаление отзыва с id: {}", id);

        Review review = getReviewById(id);

        boolean isDeleted = reviewDbRepository.deleteReview(id);

        if (isDeleted) {
            feedEventSource.notifyFeedListeners(
                    review.getUserId(),
                    review.getReviewId(),
                    EventType.REVIEW,
                    Operation.REMOVE);
            log.info("Успешно удален отзыв с id: {}", id);
        }
        return isDeleted;
    }

    @Override
    public Review getReviewById(Long id) {
        log.info("Запрос на получение отзыва с id: {}", id);
        return reviewDbRepository.getById(id).orElseThrow(() ->
                new NotFoundException("Ошибка! Отзыва с заданным идентификатором не существует"));
    }

    @Override
    public List<Review> getReviewsByFilm(Long filmId, Integer count) {
        if (filmId == null) {
            log.info("Запрос на получение {} отзывов.", count);
            return reviewDbRepository.getAll(count);
        }
        checkFilmExist(filmId);
        log.info("Запрос на получение {} отзывов к фильму с id: {}.", count, filmId);
        return reviewDbRepository.getByFilmId(filmId, count);
    }

    @Override
    public void setLike(Long id, Long userId) {
        log.info("Пользователь с id: {} хочет поставить лайк отзыву с id: {}", userId, id);
        checkReviewExist(id);
        checkUserExist(userId);

        log.info("Пользователь с id: {} поставил лайк отзыву с id: {}", userId, id);
        reviewDbRepository.setUseful(id, userId, true);
    }

    @Override
    public void setDislike(Long id, Long userId) {
        log.info("Пользователь с id: {} хочет поставить дизлайк отзыву с id: {}", userId, id);
        checkReviewExist(id);
        checkUserExist(userId);

        log.info("Пользователь с id: {} поставил дизлайк отзыву с id: {}", userId, id);
        reviewDbRepository.setUseful(id, userId, false);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        log.info("Пользователь с id: {} хочет удалить лайк/дизлайк отзыву с id: {}", userId, id);
        checkReviewExist(id);
        checkUserExist(userId);

        log.info("Пользователь с id: {} удалил лайк/дизлайк отзыву с id: {}", userId, id);
        reviewDbRepository.deleteLike(id, userId);
    }

    private void checkReviewExist(Long reviewId) {
        reviewDbRepository.getById(reviewId).orElseThrow(() ->
                new NotFoundException("Ошибка! Не найден отзыв с id: " + reviewId));
    }

    private void checkUserExist(Long userId) {
        userDbRepository.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка! Не найден пользователь с id: " + userId));
    }

    private void checkFilmExist(Long filmId) {
        filmDbRepository.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Ошибка! Не найден фильм с id: " + filmId));
    }
}