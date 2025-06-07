package ru.practicum.shareit.booking;

import ru.practicum.shareit.exceptions.ValidationException;

public enum BookingSortStatus {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingSortStatus checkExistsBookingSortStatus(String sort) {
        try {
            return BookingSortStatus.valueOf(sort);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Статуса " + sort + " не существует!");
        }
    }
}
