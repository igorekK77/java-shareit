package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ValidationException;

public class BookingSortStatusTest {
    @Test
    public void testBookingSortStatus() {
        Assertions.assertThrows(ValidationException.class, () -> BookingSortStatus
                .checkExistsBookingSortStatus("NEWTEST"));
    }
}
