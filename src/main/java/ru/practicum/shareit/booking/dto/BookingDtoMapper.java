package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoMapper;

public class BookingDtoMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(UserDtoMapper.toUserDto(booking.getBooker()));
        bookingDto.setItem(ItemDtoMapper.toItemDto(booking.getItem()));
        return bookingDto;
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStatus(bookingDto.getStatus());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }
}
