package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review addReview(Review review);

    Optional<Review> getById(Long id);

    List<Review> getAll(Integer count);

    List<Review> getByFilmId(Long filmId, Integer count);

    Boolean deleteReview(Long id);

    void setUseful(Long id, Long userId, Boolean isUseful);

    void deleteLike(Long id, Long userId);

    Review updateReview(Review review);
}