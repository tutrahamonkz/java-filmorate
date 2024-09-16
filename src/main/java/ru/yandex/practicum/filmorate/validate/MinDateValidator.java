package ru.yandex.practicum.filmorate.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class MinDateValidator implements ConstraintValidator<MinDate, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(MinDate constraintAnnotation) {
        minDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(minDate);
    }
}
