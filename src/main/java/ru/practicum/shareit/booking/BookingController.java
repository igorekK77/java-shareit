package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody BookingCreateDto booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId, @RequestParam("approved") boolean isApproved) {
        return bookingService.approvedBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInformation(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        return bookingService.getBookingInformation(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL") String sort) {
        return bookingService.getUserBookings(userId, BookingDto.checkExistsBookingSortStatus(sort));
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerItemsBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String sort) {
        return bookingService.getOwnerItemsBooking(userId, BookingDto.checkExistsBookingSortStatus(sort));

    }
}
