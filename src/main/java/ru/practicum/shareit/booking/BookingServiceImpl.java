package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingCreateDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public BookingDto createBooking(Long userId, BookingCreateDto bookingCreateDto) {
        User booker = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));

        Item item = itemStorage.findById(bookingCreateDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещь с ID = " + bookingCreateDto.getItemId() + " не найдена!"));
        if (item.getAvailable() == false) {
            throw new ValidationException("Данная вещь уже забронирована!");
        }

        List<Booking> allBookingItem = bookingStorage.findAllByItemIdAndStatus(item.getId(), BookingStatus.APPROVED);
        for (Booking booking : allBookingItem) {
            LocalDateTime startNewBooking = bookingCreateDto.getStart();
            LocalDateTime endNewBooking = bookingCreateDto.getEnd();
            if (startNewBooking.equals(booking.getStart()) && endNewBooking.equals(booking.getEnd()) ||
                    endNewBooking.isAfter(booking.getStart()) && endNewBooking.isBefore(booking.getEnd()) ||
                    startNewBooking.isAfter(booking.getStart()) && startNewBooking.isBefore(booking.getEnd())) {
                throw new ValidationException("Данная вещь уже забронирована на данный период времени!");
            }
        }

        Booking booking = BookingCreateDtoMapper.toBooking(bookingCreateDto, booker, item);
        return BookingDtoMapper.toBookingDto(bookingStorage.save(booking));
    }

    @Override
    public BookingDto approvedBooking(Long userId, Long bookingId, boolean isApproved) {
        Booking booking = getBookingById(bookingId);
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Для подтверждения бронирования, оно должно находится в статусе " +
                    "WAITING!");
        }
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

        return BookingDtoMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingInformation(Long userId, Long bookingId) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId);

        Long ownerItemId = booking.getItem().getOwner().getId();
        Long bookingItemId = booking.getBooker().getId();

        if (userId.equals(ownerItemId) || userId.equals(bookingItemId)) {
            return BookingDtoMapper.toBookingDto(booking);
        }

        throw new ValidationException("У пользователя " + userId + " нет прав на изменение " +
                "бронирования: " + bookingId);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingSortStatus bookingSortStatus) {
        getUserById(userId);
        if (bookingSortStatus == BookingSortStatus.ALL) {
            return bookingStorage.findAllByBookerIdOrderByStartDesc(userId).stream()
                    .map(BookingDtoMapper::toBookingDto).toList();
        }
        BookingStatus status = getBookingStatusByParam(bookingSortStatus);
        if (bookingSortStatus == BookingSortStatus.PAST) {
            return bookingStorage.findFilterBookerPast(userId, status, LocalDateTime.now()).stream()
                    .map(BookingDtoMapper::toBookingDto).toList();
        } else if (bookingSortStatus == BookingSortStatus.FUTURE) {
            return bookingStorage.findFilterBookerFuture(userId, status, LocalDateTime.now()).stream()
                    .map(BookingDtoMapper::toBookingDto).toList();
        } else {
            return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, status).stream()
                    .map(BookingDtoMapper::toBookingDto).toList();
        }
    }

    @Override
    public List<BookingDto> getOwnerItemsBooking(Long userId, BookingSortStatus bookingSortStatus) {
        getUserById(userId);
        if (bookingSortStatus == BookingSortStatus.ALL) {
            return bookingStorage.findAllByItemOwnerIdOrderByStartDesc(userId).stream()
                    .map(BookingDtoMapper::toBookingDto).toList();
        } 
        BookingStatus status = getBookingStatusByParam(bookingSortStatus);
        List<BookingDto> totalListBooking;
        if (bookingSortStatus == BookingSortStatus.PAST) {
            totalListBooking = bookingStorage.findFilterOwnerItemBookingPast(userId, status,
                            LocalDateTime.now()).stream().map(BookingDtoMapper::toBookingDto).toList();
        } else if (bookingSortStatus == BookingSortStatus.FUTURE) {
            totalListBooking = bookingStorage.findFilterOwnerItemBookingFuture(userId, status,
                            LocalDateTime.now()).stream().map(BookingDtoMapper::toBookingDto).toList();
        }
        totalListBooking = bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        status).stream()
                .map(BookingDtoMapper::toBookingDto)
                .toList();
        if (totalListBooking.isEmpty()) {
            throw new ValidationException("У пользователя нет вещей для бронирования!");
        }
        return totalListBooking;
    }

    private User getUserById(Long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingStorage.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирования с ID = " + bookingId + " не существует!"));
    }

    private BookingStatus getBookingStatusByParam(BookingSortStatus status) {
        return switch (status) {
            case BookingSortStatus.CURRENT -> BookingStatus.APPROVED;
            case BookingSortStatus.PAST -> BookingStatus.CANCELED;
            case BookingSortStatus.FUTURE, BookingSortStatus.WAITING -> BookingStatus.WAITING;
            case BookingSortStatus.REJECTED -> BookingStatus.REJECTED;
            case BookingSortStatus.ALL -> throw new ValidationException("Этого статуса тут не может быть!");
        };
    }
}
