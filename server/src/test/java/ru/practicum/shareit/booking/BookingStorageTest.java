package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class BookingStorageTest {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void afterEach() {

        jdbcTemplate.execute("ALTER SEQUENCE bookings_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE items_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE users_id_seq RESTART WITH 1");
    }

    @Test
    void testSaveBooking() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        Item item = itemStorage.save(new Item(null, "test1", "testDescription1", true,
                user, null));
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user, BookingStatus.WAITING);
        Booking bookingWithOutId = new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user, BookingStatus.WAITING);

        Assertions.assertEquals(booking, bookingStorage.save(bookingWithOutId));
    }

    @Test
    void testFindBookingById() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        Item item = itemStorage.save(new Item(null, "test1", "testDescription1", true,
                user, null));
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);

        Booking booking = bookingStorage.save(new Booking(null, startTime, endTime, item, user,
                BookingStatus.WAITING));
        Booking checkBooking = new Booking(1L, startTime, endTime, item, user, BookingStatus.WAITING);
        Assertions.assertEquals(Optional.of(checkBooking), bookingStorage.findById(booking.getId()));
    }

    @Test
    void testFindAllByBookerId() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        Item item = itemStorage.save(new Item(null, "test1", "testDescription1", true,
                user, null));
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);

        bookingStorage.save(new Booking(null, startTime, endTime, item, user,
                BookingStatus.WAITING));
        Booking checkBooking = new Booking(1L, startTime, endTime, item, user, BookingStatus.WAITING);
        Assertions.assertEquals(List.of(checkBooking), bookingStorage.findAllByBookerId(user.getId()));
    }

    @Test
    void testFindFilterBookerPast() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        Item item = itemStorage.save(new Item(null, "test1", "testDescription1", true,
                user, null));
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);

        bookingStorage.save(new Booking(null, startTime, endTime, item, user,
                BookingStatus.CANCELED));
        Booking checkBooking = new Booking(1L, startTime, endTime, item, user, BookingStatus.CANCELED);
        Assertions.assertEquals(List.of(checkBooking), bookingStorage.findFilterBookerPast(user.getId(),
                BookingStatus.CANCELED, LocalDateTime.now()));
    }

    @Test
    void testFindFilterBookerFuture() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        Item item = itemStorage.save(new Item(null, "test1", "testDescription1", true,
                user, null));
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);

        bookingStorage.save(new Booking(null, startTime, endTime, item, user,
                BookingStatus.WAITING));
        Booking checkBooking = new Booking(1L, startTime, endTime, item, user, BookingStatus.WAITING);
        Assertions.assertEquals(List.of(checkBooking), bookingStorage.findFilterBookerPast(user.getId(),
                BookingStatus.WAITING, LocalDateTime.now()));
    }
}
