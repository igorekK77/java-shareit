package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemStorage itemStorage;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void testCreateBooking() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        BookingCreateDto bookingCreateDto = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item.getId());
        Booking booking = new Booking(9L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.CANCELED);

        when(bookingStorage.findAllByItemIdAndStatus(item.getId(), BookingStatus.APPROVED))
                .thenReturn(List.of(booking));
        Booking totalBooking = BookingMapper.toBookingFromBookingCreateDto(bookingCreateDto, user1, item);
        when(bookingStorage.save(any(Booking.class))).thenReturn(totalBooking);

        Assertions.assertEquals(BookingMapper.toBookingDto(totalBooking), bookingService.createBooking(1L,
                bookingCreateDto));
        verify(bookingStorage,times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBookingWithNotUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        BookingCreateDto bookingCreateDto = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                1L);
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L,
                bookingCreateDto));
    }

    @Test
    void testCreateBookingWithNotItem() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        BookingCreateDto bookingCreateDto = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                1L);
        when(itemStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L,
                bookingCreateDto));
    }

    @Test
    void testCreateBookingWithFalseAvailable() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", false, new User(1L,
                "testUser", "test@mail.ru"), null);
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        BookingCreateDto bookingCreateDto = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item.getId());
        Assertions.assertThrows(ValidationException.class, () -> bookingService.createBooking(1L,
                bookingCreateDto));
    }

    @Test
    void testCreateBookingWithBookingItemOccupied() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        BookingCreateDto bookingCreateDto = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item.getId());
        Booking booking = new Booking(9L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(4),
                item, user1, BookingStatus.CANCELED);

        when(bookingStorage.findAllByItemIdAndStatus(item.getId(), BookingStatus.APPROVED))
                .thenReturn(List.of(booking));

        Assertions.assertThrows(ValidationException.class, () -> bookingService.createBooking(1L,
                bookingCreateDto));
    }

    @Test
    void testApprovedBooking() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.WAITING);
        Booking totalBooking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.APPROVED);
        when(bookingStorage.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);
        Assertions.assertEquals(BookingMapper.toBookingDto(totalBooking), bookingService.approvedBooking(1L,
                1L, true));
        verify(bookingStorage,times(1)).save(any(Booking.class));
    }

    @Test
    void testApprovedBookingWithNotBooking() {
        when(bookingStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.approvedBooking(1L, 1L,
                true));
    }

    @Test
    void testApprovedBookingWithStatusNotWaiting() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.CANCELED);
        when(bookingStorage.findById(1L)).thenReturn(Optional.of(booking));
        Assertions.assertThrows(ValidationException.class, () -> bookingService.approvedBooking(1L, 1L,
                true));
    }

    @Test
    void testApprovedBookingWithIsApprovedFalse() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.WAITING);
        Booking totalBooking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.REJECTED);
        when(bookingStorage.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);
        Assertions.assertEquals(BookingMapper.toBookingDto(totalBooking), bookingService.approvedBooking(1L,
                1L, false));
    }

    @Test
    void testApprovedBookingWithUserNotOwnerItem() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.WAITING);
        Booking totalBooking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.REJECTED);
        when(bookingStorage.findById(1L)).thenReturn(Optional.of(booking));
        Assertions.assertThrows(ValidationException.class, () -> bookingService.approvedBooking(3L, 1L,
                true));
    }

    @Test
    void testGetBookingInformation() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.APPROVED);
        when(bookingStorage.findById(1L)).thenReturn(Optional.of(booking));
        Assertions.assertEquals(BookingMapper.toBookingDto(booking), bookingService.getBookingInformation(1L,
                1L));
    }

    @Test
    void testGetBookingInformationWithNotUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBookingInformation(1L,
                1L));
    }

    @Test
    void testGetBookingInformationWithNotBooking() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, ()-> bookingService.getBookingInformation(1L,
                1L));
    }

    @Test
    void testGetBookingInformationWithNotAccess() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        User user2 = new User(22L, "testUser22", "test22@mail.ru");
        when(userStorage.findById(22L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.APPROVED);
        when(bookingStorage.findById(1L)).thenReturn(Optional.of(booking));
        Assertions.assertThrows(ValidationException.class, () -> bookingService.getBookingInformation(22L,
                1L));
    }

    @Test
    void testGetUserBookingsAll() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.CANCELED);
        Booking booking2 = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(3L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, user1, BookingStatus.WAITING);
        when(bookingStorage.findAllByBookerIdOrderByStartDesc(user1.getId())).thenReturn(List.of(booking3, booking2,
                booking1));
        Assertions.assertEquals(List.of(booking3, booking2, booking1).stream().map(BookingMapper::toBookingDto)
                        .toList(), bookingService.getUserBookings(1L, BookingSortStatus.ALL));
    }

    @Test
    void testGetUserBookingsWithNotUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(1L,
                BookingSortStatus.ALL));
    }

    @Test
    void testGetUserBookingsPast() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.CANCELED);
        when(bookingStorage.findFilterBookerPast(eq(user1.getId()), eq(BookingStatus.CANCELED),
                any(LocalDateTime.class))).thenReturn(List.of(booking1));
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking1)),
                bookingService.getUserBookings(1L, BookingSortStatus.PAST));
    }

    @Test
    void testGetUserBookingsFuture() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking = new Booking(3L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, user1, BookingStatus.WAITING);
        when(bookingStorage.findFilterBookerFuture(eq(user1.getId()), eq(BookingStatus.WAITING),
                any(LocalDateTime.class))).thenReturn(List.of(booking));
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)),
                bookingService.getUserBookings(1L, BookingSortStatus.FUTURE));
    }

    @Test
    void testGetUserBookingsApproved() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking = new Booking(3L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, user1, BookingStatus.APPROVED);
        when(bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(user1.getId(), BookingStatus.APPROVED))
                .thenReturn(List.of(booking));
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)),
                bookingService.getUserBookings(1L, BookingSortStatus.CURRENT));
    }

    @Test
    void testGetUserBookingsRejected() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking = new Booking(3L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, user1, BookingStatus.REJECTED);
        when(bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(user1.getId(), BookingStatus.REJECTED))
                .thenReturn(List.of(booking));
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking)),
                bookingService.getUserBookings(1L, BookingSortStatus.REJECTED));
    }

    @Test
    void testGetOwnerItemsBooking() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.CANCELED);
        Booking booking2 = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(3L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, user1, BookingStatus.WAITING);
        when(bookingStorage.findAllByItemOwnerIdOrderByStartDesc(user1.getId())).thenReturn(List.of(booking3, booking2,
                booking1));
        Assertions.assertEquals(List.of(booking3, booking2, booking1).stream().map(BookingMapper::toBookingDto)
                .toList(), bookingService.getOwnerItemsBooking(1L, BookingSortStatus.ALL));
    }

    @Test
    void testGetOwnerItemsBookingWithNotUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getOwnerItemsBooking(1L,
                BookingSortStatus.ALL));
    }

    @Test
    void testGetOwnerItemsBookingPast() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, user1, BookingStatus.CANCELED);
        when(bookingStorage.findFilterOwnerItemBookingPast(eq(user1.getId()), eq(BookingStatus.CANCELED),
                any(LocalDateTime.class))).thenReturn(List.of(booking1));
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking1)),
                bookingService.getOwnerItemsBooking(1L, BookingSortStatus.PAST));
    }

    @Test
    void testGetOwnerItemsBookingFuture() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, user1, BookingStatus.WAITING);
        when(bookingStorage.findFilterOwnerItemBookingFuture(eq(user1.getId()), eq(BookingStatus.WAITING),
                any(LocalDateTime.class))).thenReturn(List.of(booking1));
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking1)),
                bookingService.getOwnerItemsBooking(1L, BookingSortStatus.FUTURE));
    }

    @Test
    void testGetOwnerItemsBookingApproved() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, user1, BookingStatus.APPROVED);
        when(bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(user1.getId(), BookingStatus.APPROVED))
                .thenReturn(List.of(booking1));
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking1)),
                bookingService.getOwnerItemsBooking(1L, BookingSortStatus.CURRENT));
    }

    @Test
    void testGetOwnerItemsBookingRejected() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, user1, BookingStatus.REJECTED);
        when(bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(user1.getId(), BookingStatus.REJECTED))
                .thenReturn(List.of(booking1));
        Assertions.assertEquals(List.of(BookingMapper.toBookingDto(booking1)),
                bookingService.getOwnerItemsBooking(1L, BookingSortStatus.REJECTED));
    }

    @Test
    void testGetOwnerItemsBookingNotHaveItemsBooking() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, user1, BookingStatus.REJECTED);
        when(bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(user1.getId(), BookingStatus.REJECTED))
                .thenReturn(List.of());
        Assertions.assertThrows(ValidationException.class, () -> bookingService.getOwnerItemsBooking(1L,
                BookingSortStatus.REJECTED));
    }

    @Test
    void testGetBookingStatusByParamAll() {
        Assertions.assertThrows(ValidationException.class, () -> bookingService
                .getBookingStatusByParam(BookingSortStatus.ALL));
    }
}
