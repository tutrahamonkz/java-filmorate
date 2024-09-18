package ru.yandex.practicum.filmorate.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

// Реализация валидатора для аннотации MinDate
public class MinDateValidator implements ConstraintValidator<MinDate, LocalDate> {
    private LocalDate minDate; // Переменная для хранения минимально допустимой даты

    @Override
    public void initialize(MinDate constraintAnnotation) {
        minDate = LocalDate.parse(constraintAnnotation.value()); // Парсинг значения минимальной даты из аннотации
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        // Если дата равна null, считаем ее допустимой (игнорируем проверку)
        if (localDate == null) {
            return true;
        }
        // Проверяем, что дата после минимально допустимой
        return localDate.isAfter(minDate);
    }
}