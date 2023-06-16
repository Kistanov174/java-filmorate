package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class LoginValidator implements ConstraintValidator<Login, String> {
    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if (Objects.nonNull(login) && login.contains(" ")) {
            return false;
        }
        return true;
    }
}