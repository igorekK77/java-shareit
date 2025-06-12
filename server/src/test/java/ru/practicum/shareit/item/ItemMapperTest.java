package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemAnswerRequestDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;

public class ItemMapperTest {
    @Test
    public void toItemAnswerRequestDtoTest() {
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        ItemAnswerRequestDto dto = new ItemAnswerRequestDto(1L, "test1", 1L);
        ItemMapper mapper = new ItemMapper();
        Assertions.assertEquals(dto, mapper.toItemAnswerRequestDto(item));
    }
}
