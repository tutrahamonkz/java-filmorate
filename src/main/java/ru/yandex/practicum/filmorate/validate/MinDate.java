package ru.yandex.practicum.filmorate.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinDateValidator.class)
public @interface MinDate {
    String message() default "{value.negative}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value() default "1895-12-28";
}
