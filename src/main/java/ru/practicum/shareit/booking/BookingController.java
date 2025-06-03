package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody BookingCreateDto booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public Booking approvedBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId, @RequestParam("approved") boolean isApproved) {
        return bookingService.approvedBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingInformation(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        return bookingService.getBookingInformation(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL") String sort) {
        return bookingService.getUserBookings(userId, sort);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerItemsBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String sort) {
        List<BookingDto> ownerBooking = bookingService.getOwnerItemsBooking(userId, sort);
        if (ownerBooking.isEmpty()) {
            throw new ValidationException("У пользователя нет вещей для бронирования!");
        }
        return ownerBooking;
    }
}
