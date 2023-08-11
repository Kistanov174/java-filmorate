package ru.yandex.practicum.filmorate.exception;

public class ErrorResponse {
    private final String name;
    private final String description;

    public ErrorResponse(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}