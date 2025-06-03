package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public Booking createBooking(Long userId, BookingCreateDto bookingCreateDto) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());

        User booker = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));

        Item item = itemStorage.findById(bookingCreateDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещь с ID = " + bookingCreateDto.getItemId() + " не найдена!"));
        if (item.getAvailable() == false) {
            throw new ValidationException("Данная вещь уже забронирована!");
        }

        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingStorage.save(booking);
    }

    public Booking approvedBooking(Long userId, Long bookingId, boolean isApproved) {
        Booking booking = getBookingById(bookingId);

        if (isApproved) {
            if (!booking.getItem().getOwner().getId().equals(userId)) {
                throw new ValidationException("У пользователя " + userId + " нет прав на изменение " +
                        "бронирования: " + bookingId);
            }
            booking.setStatus(BookingStatus.APPROVED);
            bookingStorage.save(booking);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingStorage.save(booking);
        }

        return booking;
    }

    public Booking getBookingInformation(Long userId, Long bookingId) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId);

        Long ownerItemId = booking.getItem().getOwner().getId();
        Long bookingItemId = booking.getBooker().getId();

        if (userId.equals(ownerItemId) || userId.equals(bookingItemId)) {
            return booking;
        }

        throw new ValidationException("У пользователя " + userId + " нет прав на изменение " +
                "бронирования: " + bookingId);
    }

    public List<Booking> getUserBookings(Long userId, String stringParamsStatus) {
        getUserById(userId);
        if (stringParamsStatus.equals("ALL")) {
            return bookingStorage.findAllByBookerId(userId);
        }
        BookingStatus status = getBookingStatusByParam(stringParamsStatus);
        return bookingStorage.findAllByBookerIdAndStatus(userId, status);
    }

    public List<BookingDto> getOwnerItemsBooking(Long userId, String stringParamsStatus) {
        getUserById(userId);
        if (stringParamsStatus.equals("ALL")) {
            return bookingStorage.findAllByItemOwnerId(userId).stream().map(BookingDto::toBookingDto).toList();
        }
        BookingStatus status = getBookingStatusByParam(stringParamsStatus);
        return bookingStorage.findAllByItemOwnerIdAndStatus(userId, status).stream().map(BookingDto::toBookingDto)
                .toList();
    }

    private User getUserById(Long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingStorage.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирования с ID = " + bookingId + " не существует!"));
    }

    private BookingStatus getBookingStatusByParam(String status) {
        switch (status) {
            case "CURRENT":
                return BookingStatus.APPROVED;
            case "PAST":
                return BookingStatus.CANCELED;
            case "FUTURE", "WAITING":
                return BookingStatus.WAITING;
            case "REJECTED":
                return BookingStatus.REJECTED;
        }
        throw new ValidationException("Статуса " + status + " не существует!");
    }
}
