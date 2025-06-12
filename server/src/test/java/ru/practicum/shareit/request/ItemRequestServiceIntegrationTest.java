package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void testCreateItemRequest() {
        ItemRequestDescriptionDto itemRequestDescriptionDto = new ItemRequestDescriptionDto("test description");
        CreateUserDto userCreateDto = new CreateUserDto("Test1", "test1@mail.ru");
        UserDto user = userService.createUser(userCreateDto);
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(user.getId(), itemRequestDescriptionDto);
        ItemRequestDto checkItemRequestDto = new ItemRequestDto(itemRequestDto.getId(), "test description",
                user.getId(), itemRequestDto.getCreated());
        Assertions.assertEquals(checkItemRequestDto, itemRequestDto);
    }

    @Test
    void testGetRequestUser() {
        ItemRequestDescriptionDto itemRequestDescriptionDto = new ItemRequestDescriptionDto("test description");
        CreateUserDto userCreateDto = new CreateUserDto("Test1", "test1@mail.ru");
        UserDto user = userService.createUser(userCreateDto);
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(user.getId(), itemRequestDescriptionDto);
        ItemRequestWithAnswerDto itemRequestWithAnswerDto = new ItemRequestWithAnswerDto(itemRequestDto.getId(),
                "test description", user.getId(), itemRequestDto.getCreated(), null);
        Assertions.assertEquals(List.of(itemRequestWithAnswerDto), itemRequestService.getRequestUser(user.getId()));
    }
}
