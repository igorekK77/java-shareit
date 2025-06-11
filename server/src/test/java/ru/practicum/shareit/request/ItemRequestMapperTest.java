package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class ItemRequestMapperTest {

    @Test
    void testToDto() {
        ItemRequestMapper mapper = new ItemRequestMapper();
        User user1 = new User(1L, "testUser", "test@mail.ru");
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequest1 = new ItemRequest(1L, "test description", user1, dateTime);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "test description",
                user1.getId(), dateTime);
        Assertions.assertEquals(itemRequestDto, mapper.toDto(itemRequest1));
    }
}
