package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Objects;

public class BirthdayValidator implements ConstraintValidator<Birthday, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (Objects.nonNull(date)) {
            return date.isBefore(LocalDate.now());
        }
        return true;
    }
}