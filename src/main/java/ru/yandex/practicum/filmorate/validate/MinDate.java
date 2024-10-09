package ru.yandex.practicum.filmorate.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // Указываем, что аннотация может применяться к полям
@Retention(RetentionPolicy.RUNTIME) // Указываем, что аннотация будет доступна в рантайме
@Constraint(validatedBy = {MinDateValidator.class}) // Указываем, что эта аннотация является ограничением (constraint)
public @interface MinDate {
    // Сообщение по умолчанию, которое будет возвращено при нарушении ограничения
    String message() default "{MinDate.message}";

    // Группы, к которым может принадлежать это ограничение (для групповой валидации)
    Class<?>[] groups() default {};

    // Дополнительные данные, которые могут быть переданы вместе с ограничением
    Class<? extends Payload>[] payload() default {};

    // Минимально допустимая дата по умолчанию
    String value() default "0001-01-01";
}