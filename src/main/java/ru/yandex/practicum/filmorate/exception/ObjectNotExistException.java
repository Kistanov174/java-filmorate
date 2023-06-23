package ru.yandex.practicum.filmorate.exception;

public class ObjectNotExistException extends RuntimeException {
    public ObjectNotExistException(String message) {
        super(message);
    }

    public ObjectNotExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ObjectNotExistException(final Throwable cause) {
        super(cause);
    }
}