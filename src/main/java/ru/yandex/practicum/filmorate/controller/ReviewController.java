package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review newReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Boolean deleteReview(@PathVariable @Positive Long id) {
        return reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable @Positive Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewsByFilm(@RequestParam(value = "filmId", required = false) Long filmId,
                                         @RequestParam(value = "count", defaultValue = "10") Integer count) {
        return reviewService.getReviewsByFilm(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable @Positive Long id,
                        @PathVariable @Positive Long userId) {
        reviewService.setLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void setDislike(@PathVariable @Positive Long id,
                           @PathVariable @Positive Long userId) {
        reviewService.setDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable @Positive Long id,
                           @PathVariable @Positive Long userId) {
        reviewService.deleteLike(id, userId);
    }
}