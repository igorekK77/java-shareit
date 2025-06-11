package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class BookingMapperTest {
    @Test
    public void testToBookingFromBookingDto() {
        BookingMapper bookingMapper = new BookingMapper();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        Booking booking = new Booking(1L, start, end, null, null, BookingStatus.WAITING);
        BookingDto bookingDto = new BookingDto(1L, start, end, null,
                null, BookingStatus.WAITING);
        Assertions.assertEquals(booking, bookingMapper.toBookingFromBookingDto(bookingDto));
    }

    @Test
    public void testToBookingCreateDto() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        User user1 = new User(1L, "testUser", "test@mail.ru");
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking = new Booking(1L, start, end, item, user1, BookingStatus.WAITING);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(start, end, item.getId());
        Assertions.assertEquals(bookingCreateDto, BookingMapper.toBookingCreateDto(booking));
    }
}
