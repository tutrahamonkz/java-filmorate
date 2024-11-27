package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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

    @Override
    public Review addReview(Review review) {
        checkFilmExist(review.getFilmId());
        checkUserExist(review.getUserId());
        Review addedReview = reviewDbRepository.addReview(review);
        return addedReview;
    }

    @Override
    public Review updateReview(Review review) {
        checkFilmExist(review.getFilmId());
        checkUserExist(review.getUserId());
        checkReviewExist(review.getReviewId());
        Review updatedReview = reviewDbRepository.updateReview(review);
        return updatedReview;
    }

    @Override
    public Boolean deleteReview(Long id) {
        return reviewDbRepository.deleteReview(id);
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewDbRepository.getById(id).orElseThrow(() ->
                new NotFoundException("Ошибка! Отзыва с заданным идентификатором не существует"));
    }

    @Override
    public List<Review> getReviewsByFilm(Long filmId, Integer count) {
        if (filmId == null) {
            return reviewDbRepository.getAll(count);
        }
        checkFilmExist(filmId);
        return reviewDbRepository.getByFilmId(filmId, count);
    }

    @Override
    public void setLike(Long id, Long userId) {
        checkReviewExist(id);
        checkUserExist(userId);
        reviewDbRepository.setUseful(id, userId, true);
    }

    @Override
    public void setDislike(Long id, Long userId) {
        checkReviewExist(id);
        checkUserExist(userId);
        reviewDbRepository.setUseful(id, userId, false);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        checkReviewExist(id);
        checkUserExist(userId);
        reviewDbRepository.deleteLike(id, userId);
    }

    private void checkReviewExist(Long reviewId) {
        reviewDbRepository.getById(reviewId).orElseThrow(() ->
                new NotFoundException("Ошибка! Отзыва с заданным идентификатором не существует"));
    }

    private void checkUserExist(Long userId) {
        userDbRepository.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
    }

    private void checkFilmExist(Long filmId) {
        filmDbRepository.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));
    }
}