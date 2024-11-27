package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Review review);

    Review updateReview(Review review);

    Boolean deleteReview(Long id);

    Review getReviewById(Long id);

    List<Review> getReviewsByFilm(Long filmId, Integer count);

    void setLike(Long id, Long userId);

    void setDislike(Long id, Long userId);

    void deleteLike(Long id, Long userId);
}
