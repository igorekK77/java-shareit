package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingSortStatus;
import ru.practicum.shareit.exceptions.ValidationException;

public class BookingSortStatusTest {
    @Test
    void testCheckExistsBookingSortStatus() {
        Assertions.assertEquals(BookingSortStatus.ALL, BookingSortStatus.checkExistsBookingSortStatus("ALL"));
        Assertions.assertEquals(BookingSortStatus.CURRENT, BookingSortStatus.checkExistsBookingSortStatus("CURRENT"));
        Assertions.assertEquals(BookingSortStatus.PAST, BookingSortStatus.checkExistsBookingSortStatus("PAST"));
        Assertions.assertEquals(BookingSortStatus.FUTURE, BookingSortStatus.checkExistsBookingSortStatus("FUTURE"));
        Assertions.assertEquals(BookingSortStatus.WAITING, BookingSortStatus.checkExistsBookingSortStatus("WAITING"));
        Assertions.assertEquals(BookingSortStatus.REJECTED, BookingSortStatus.checkExistsBookingSortStatus("REJECTED"));
    }

    @Test
    void testCheckExistsBookingSortStatusWithStatusNotExist() {
        Assertions.assertThrows(ValidationException.class, () -> BookingSortStatus
                .checkExistsBookingSortStatus("TEEST"));

    }
}
