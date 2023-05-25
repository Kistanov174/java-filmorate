package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Objects;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    private static final int OLDEST_RELEASE_YEAR = 1895;
    private static final int OLDEST_RELEASE_MONTH = 12;
    private static final int OLDEST_RELEASE_DAY = 28;

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (Objects.nonNull(date)) {
            return date.isAfter(LocalDate.of(OLDEST_RELEASE_YEAR, OLDEST_RELEASE_MONTH, OLDEST_RELEASE_DAY));
        }
        return false;
    }
}