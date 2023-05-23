package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BirthdayValidator implements ConstraintValidator<Birthday, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date != null) {
            return date.isBefore(LocalDate.now());
        }
        return true;
    }
}
