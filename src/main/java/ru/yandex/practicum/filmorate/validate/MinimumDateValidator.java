package ru.yandex.practicum.filmorate.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinimumDateValidator implements ConstraintValidator<MinDate, LocalDate> {
    private final static LocalDate MINIMUM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(MINIMUM_RELEASE_DATE);
    }
}
