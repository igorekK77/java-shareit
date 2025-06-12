package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void testCreateItem() {
        CreateUserDto userCreateDto = new CreateUserDto("Test1", "test1@mail.ru");
        UserDto user = userService.createUser(userCreateDto);
        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", true,
                null);
        ItemDto createdItem = itemService.createItem(user.getId(), itemCreateDto);
        ItemDto checkItem = new ItemDto(createdItem.getId(), "test1", "testDescription1",
                true, user.getId(), null);
        Assertions.assertEquals(checkItem, createdItem);
    }

    @Test
    void testGetItemById() {
        CreateUserDto userCreateDto = new CreateUserDto("Test1", "test1@mail.ru");
        UserDto user = userService.createUser(userCreateDto);
        User user1 = UserMapper.toUser(user);
        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", true,
                null);
        ItemDto createdItem = itemService.createItem(user.getId(), itemCreateDto);
        ItemDtoWithBookings checkItem = new ItemDtoWithBookings(createdItem.getId(), "test1",
                "testDescription1", true, user1, null, null, List.of());
        Assertions.assertEquals(checkItem, itemService.getItemById(user.getId(), createdItem.getId()));
    }
}
