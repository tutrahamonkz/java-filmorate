package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mapper.ReviewRowMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            "ORDER BY useful DESC LIMIT :count;";

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
            "is_positive = ?, " +
            "user_id = ?, " +
            "film_id = ? " +
            "WHERE review_id = ?";

    @Override
    public Review addReview(Review review) {
        Long id = insert(SQL_INSERT_REVIEW, review.getContent(), review.getIsPositive(), review.getFilmId(),
                review.getUserId());
        review.setReviewId(id);
        return review;
    }

    @Override
    public Optional<Review> getById(Long id) {
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
        Map<String, Object> params = Map.of("count", count);
        return findMany(SQL_GET_ALL_REVIEWS_LIMIT, params);
    }

    @Override
    public List<Review> getByFilmId(Long filmId, Integer count) {
        return findMany(SQL_GET_REVIEW_BY_FILM_IDS_LIMIT, filmId, count);
    }

    @Override
    public Boolean deleteReview(Long id) {
        return delete(SQL_DELETE_REVIEW_BY_ID, id);
    }

    @Override
    public void setUseful(Long id, Long userId, Boolean isUseful) {
        update(SQL_MERGE_REVIEW_LIKES, id, userId, isUseful);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        Map<String, Object> params = Map.of(
                "review_id", id,
                "user_id", userId);
        delete(SQL_DELETE_LIKE, id, userId);
    }

    @Override
    public Review updateReview(Review review) {
        jdbc.update(SQL_UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getUserId(),
                review.getFilmId(), review.getReviewId());
        return getById(review.getReviewId()).get();
    }
}