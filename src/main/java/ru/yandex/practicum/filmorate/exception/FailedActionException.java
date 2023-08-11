package ru.yandex.practicum.filmorate.exception;

public class FailedActionException extends RuntimeException {
    public FailedActionException(String message) {
        super(message);
    }

    public FailedActionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FailedActionException(final Throwable cause) {
        super(cause);
    }
}