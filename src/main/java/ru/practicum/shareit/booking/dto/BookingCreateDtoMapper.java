package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingCreateDtoMapper {
    public static BookingCreateDto toBookingCreateDto(Booking booking) {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(booking.getItem().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }

    public static Booking toBooking(BookingCreateDto dto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }
}
