package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void testCreateBooking() {
        CreateUserDto userCreateDto = new CreateUserDto("Test1", "test1@mail.ru");
        UserDto userOwner = userService.createUser(userCreateDto);

        CreateUserDto userCreateDto2 = new CreateUserDto("Test2", "test2@mail.ru");
        UserDto userBooking = userService.createUser(userCreateDto2);


        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", true,
                null);
        ItemDto item = itemService.createItem(userOwner.getId(), itemCreateDto);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusDays(1);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(startTime, endTime,
                item.getId());
        BookingDto bookingDto = bookingService.createBooking(userBooking.getId(), bookingCreateDto);
        BookingDto checkBooking = new BookingDto(bookingDto.getId(), startTime, endTime, item, userBooking,
                BookingStatus.WAITING);
        Assertions.assertEquals(checkBooking, bookingDto);
    }

    @Test
    void testApproveBooking() {
        CreateUserDto userCreateDto = new CreateUserDto("Test1", "test1@mail.ru");
        UserDto userOwner = userService.createUser(userCreateDto);

        CreateUserDto userCreateDto2 = new CreateUserDto("Test2", "test2@mail.ru");
        UserDto userBooking = userService.createUser(userCreateDto2);


        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", true,
                null);
        ItemDto item = itemService.createItem(userOwner.getId(), itemCreateDto);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusDays(1);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(startTime, endTime,
                item.getId());
        BookingDto bookingDto = bookingService.createBooking(userBooking.getId(), bookingCreateDto);

        BookingDto totalBooking = bookingService.approvedBooking(userOwner.getId(), bookingDto.getId(), true);
        Assertions.assertEquals(BookingStatus.APPROVED, totalBooking.getStatus());
    }
}
