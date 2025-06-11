package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingCreateDto bookingCreateDto);

    BookingDto approvedBooking(Long userId, Long bookingId, boolean isApproved);

    BookingDto getBookingInformation(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, BookingSortStatus bookingSortStatus);

    List<BookingDto> getOwnerItemsBooking(Long userId, BookingSortStatus bookingSortStatus);
}
