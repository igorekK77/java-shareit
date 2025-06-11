package ru.practicum.shareit.booking;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreateDto;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
												@RequestBody BookingCreateDto booking) {
		return bookingClient.createBooking(userId, booking);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approvedBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
												  @PathVariable Long bookingId,
												  @RequestParam("approved") boolean isApproved) {
		return bookingClient.approvedBooking(userId, bookingId, isApproved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingInformation(@RequestHeader("X-Sharer-User-Id") Long userId,
													   @PathVariable Long bookingId) {
		return bookingClient.getBookingInformation(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
												  @RequestParam(defaultValue = "ALL") String sort) {
		return bookingClient.getUserBookings(userId, sort);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerItemsBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
													   @RequestParam(defaultValue = "ALL") String sort) {
		return bookingClient.getOwnerItemsBooking(userId, sort);
	}

}
