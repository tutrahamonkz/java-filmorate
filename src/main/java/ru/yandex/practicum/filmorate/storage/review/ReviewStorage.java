package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mapper.ReviewRowMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ReviewStorage extends BaseStorage<Review> implements ReviewRepository {

    private final ReviewRowMapper reviewRowMapper;

    public ReviewStorage(JdbcTemplate jdbc, RowMapper<Review> mapper,
                         ReviewRowMapper reviewRowMapper) {
        super(jdbc, mapper, Review.class);
        this.reviewRowMapper = reviewRowMapper;
    }

    private static final String SQL_INSERT_REVIEW =
            "INSERT INTO reviews (content, is_positive, film_id, user_id) " +
                    "VALUES (?, ?, ?, ?);";

    private static final String SQL_GET_REVIEW_BASE =
            "SELECT pos.review_id, pos.content, pos.film_id, pos.user_id, pos.is_positive, (pos.positive - neg.negative)" +
                    " AS useful FROM " +
                    "(SELECT r.review_id, r.content, r.film_id, r.user_id, r.is_positive, COUNT(pos.review_id) " +
                    "AS positive FROM REVIEWS r " +
                    "LEFT JOIN " +
                    "(SELECT rl.review_id, rl.is_useful FROM REVIEWS_LIKES rl " +
                    "WHERE rl.IS_USEFUL = TRUE) pos " +
                    "ON r.REVIEW_ID = pos.review_id " +
                    "GROUP BY r.review_id) pos " +
                    "JOIN " +
                    "(SELECT r.review_id, r.content, r.film_id, r.user_id, r.is_positive, COUNT(neg.review_id) AS negative FROM REVIEWS r " +
                    "LEFT JOIN " +
                    "(SELECT rl.review_id, rl.is_useful FROM REVIEWS_LIKES rl " +
                    "WHERE rl.IS_USEFUL = FALSE) neg " +
                    "ON r.REVIEW_ID = neg.review_id " +
                    "GROUP BY r.review_id) neg " +
                    "ON pos.review_id = neg.review_id ";

    private static final String SQL_GET_REVIEW_BY_IDS = SQL_GET_REVIEW_BASE +
            "WHERE pos.review_id IN (?) ORDER BY useful DESC;";

    private static final String SQL_GET_ALL_REVIEWS_LIMIT = SQL_GET_REVIEW_BASE +
            "ORDER BY useful DESC LIMIT ?";

    private static final String SQL_GET_REVIEW_BY_FILM_IDS_LIMIT = SQL_GET_REVIEW_BASE
            + "WHERE pos.film_id = ? ORDER BY useful DESC LIMIT ?";

    private static final String SQL_DELETE_REVIEW_BY_ID =
            "DELETE FROM reviews WHERE review_id = ?";

    private static final String SQL_MERGE_REVIEW_LIKES =
            "MERGE INTO reviews_likes (review_id, user_id, is_useful) " +
                    "VALUES (?, ?, ?)";

    private static final String SQL_DELETE_LIKE =
            "DELETE FROM reviews_likes WHERE (review_id = ? AND user_id = ?)";

    private static final String SQL_UPDATE_REVIEW = "UPDATE reviews SET content = ?, " +
            "is_positive = ? " +
            "WHERE review_id = ?";

    @Override
    public Review addReview(Review review) {
        log.info("Добавление отзыва от пользователя с id: {} к фильму с id: {}",
                review.getUserId(), review.getFilmId());
        Long id = insert(SQL_INSERT_REVIEW, review.getContent(), review.getIsPositive(), review.getFilmId(),
                review.getUserId());
        review.setReviewId(id);
        log.info("Успешно добавлен отзыв с id: {}, от пользователя с id: {} к фильму с id: {}",
                review.getReviewId(), review.getUserId(), review.getFilmId());
        return review;
    }

    @Override
    public Optional<Review> getById(Long id) {
        log.info("Запрос на получение отзыва с id: {}", id);
        Optional<Review> review;
        try {
            review = findOne(SQL_GET_REVIEW_BY_IDS, id);
        } catch (EmptyResultDataAccessException ignored) {
            review = Optional.empty();
        }
        return review;
    }

    @Override
    public List<Review> getAll(Integer count) {
        log.info("Запрос на получение {} отзывов.", count);
        return findMany(SQL_GET_ALL_REVIEWS_LIMIT, count);
    }

    @Override
    public List<Review> getByFilmId(Long filmId, Integer count) {
        log.info("Запрос на получение {} отзывов к фильму с id: {}.", count, filmId);
        return findMany(SQL_GET_REVIEW_BY_FILM_IDS_LIMIT, filmId, count);
    }

    @Override
    public Boolean deleteReview(Long id) {
        log.info("Удаление отзыва с id: {}", id);
        boolean isDelete = delete(SQL_DELETE_REVIEW_BY_ID, id);
        if (isDelete) {
            log.info("Успешно удален отзыв с id: {}", id);
        }
        return isDelete;
    }

    @Override
    public void setUseful(Long id, Long userId, Boolean isUseful) {
        if (isUseful) {
            log.info("Пользователь с id: {} хочет поставить лайк отзыву с id: {}", userId, id);
        } else {
            log.info("Пользователь с id: {} хочет поставить дизлайк отзыву с id: {}", userId, id);
        }
        update(SQL_MERGE_REVIEW_LIKES, id, userId, isUseful);
        if (isUseful) {
            log.info("Пользователь с id: {} поставил лайк отзыву с id: {}", userId, id);
        } else {
            log.info("Пользователь с id: {} поставил дизлайк отзыву с id: {}", userId, id);
        }
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        log.info("Пользователь с id: {} хочет удалить лайк/дизлайк отзыву с id: {}", userId, id);
        if (delete(SQL_DELETE_LIKE, id, userId)) {
            log.info("Пользователь с id: {} удалил лайк/дизлайк отзыву с id: {}", userId, id);
        }
    }

    @Override
    public Review updateReview(Review review) {
        log.info("Изменение отзыва с id: {} от пользователя с id: {} к фильму с id: {}",
                review.getReviewId(), review.getUserId(), review.getFilmId());

        update(SQL_UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getReviewId());

        log.info("Успешно изменен отзыв с id: {}, от пользователя с id: {} к фильму с id: {}",
                review.getReviewId(), review.getUserId(), review.getFilmId());
        return getById(review.getReviewId()).get();
    }
}