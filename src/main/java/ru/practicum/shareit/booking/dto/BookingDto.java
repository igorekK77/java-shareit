package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingSortStatus;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDto item;

    private UserDto booker;

    private BookingStatus status;

    public static BookingSortStatus checkExistsBookingSortStatus(String sort) {
        try {
            return BookingSortStatus.valueOf(sort);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Статуса " + sort + " не существует!");
        }
    }
}
