package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestStorage itemRequestStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemStorage itemStorage;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void testCreateItemRequest() {
        ItemRequestDescriptionDto itemRequestDescriptionDto = new ItemRequestDescriptionDto("test description");
        User user = new User(1L, "testUser", "test@mail.ru");
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(1L, "test description", user, dateTime);
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestStorage.save(any(ItemRequest.class))).thenReturn(itemRequest);

        Assertions.assertEquals(ItemRequestMapper.toDto(itemRequest),
                itemRequestService.createItemRequest(1L, itemRequestDescriptionDto));
        verify(itemRequestStorage).save(any(ItemRequest.class));
    }

    @Test
    void testCreateItemRequestWithNotFoundUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        ItemRequestDescriptionDto itemRequestDescriptionDto = new ItemRequestDescriptionDto("test description");
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(1L,
                itemRequestDescriptionDto));
    }

    @Test
    void testGetRequestUser() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequest1 = new ItemRequest(1L, "test description", user1, dateTime);
        ItemRequest itemRequest2 = new ItemRequest(2L, "test description2", user1, dateTime);
        when(itemRequestStorage.findAllByRequesterId(1L)).thenReturn(List.of(itemRequest1, itemRequest2));

        User user2 = new User(2L, "testUser2", "test2@mail.ru");
        Item item1 = new Item(1L, "test1", "testD1", true, user2, itemRequest1);
        Item item2 = new Item(2L, "test2", "testD2", true, user2, itemRequest2);
        Item item3 = new Item(3L, "test3", "testD3", true, user2, itemRequest2);

        when(itemStorage.findAllByItemRequestIdIn(List.of(itemRequest1.getId(), itemRequest2.getId())))
                .thenReturn(List.of(item1, item2, item3));

        ItemRequestWithAnswerDto itemRequestWithAnswerDto1 = new ItemRequestWithAnswerDto(1L,
                "test description", user1.getId(), dateTime,
                List.of(ItemMapper.toItemAnswerRequestDto(item1)));
        ItemRequestWithAnswerDto itemRequestWithAnswerDto2 = new ItemRequestWithAnswerDto(2L,
                "test description2", user1.getId(), dateTime,
                List.of(ItemMapper.toItemAnswerRequestDto(item2), ItemMapper.toItemAnswerRequestDto(item3)));
        Assertions.assertEquals(List.of(itemRequestWithAnswerDto1, itemRequestWithAnswerDto2),
                itemRequestService.getRequestUser(user1.getId()));
        verify(itemRequestStorage).findAllByRequesterId(1L);
    }

    @Test
    void testGetRequestUserWithNotFoundUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequestUser(1L));
    }

    @Test
    void testGetRequestUserWithEmptyCountRequest() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRequestStorage.findAllByRequesterId(1L)).thenReturn(List.of());
        Assertions.assertThrows(ValidationException.class, () -> itemRequestService.getRequestUser(user1.getId()));
        verify(itemRequestStorage).findAllByRequesterId(1L);
    }

    @Test
    void testGetRequestUserWithEmptyCountAnswerRequest() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequest1 = new ItemRequest(1L, "test description", user1, dateTime);
        ItemRequest itemRequest2 = new ItemRequest(2L, "test description2", user1, dateTime);
        when(itemRequestStorage.findAllByRequesterId(1L)).thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemStorage.findAllByItemRequestIdIn(List.of(itemRequest1.getId(), itemRequest2.getId())))
                .thenReturn(List.of());

        Assertions.assertEquals(List.of(ItemRequestMapper.toRequestWithAnswerDto(itemRequest1),
                        ItemRequestMapper.toRequestWithAnswerDto(itemRequest2)),
                itemRequestService.getRequestUser(user1.getId()));
        verify(itemRequestStorage).findAllByRequesterId(1L);
    }

    @Test
    void testGetAllRequests() {
        User user1 = new User(4L, "testUser", "test@mail.ru");
        when(userStorage.findById(4L)).thenReturn(Optional.of(user1));
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequest1 = new ItemRequest(1L, "test description", user1, dateTime);
        ItemRequest itemRequest2 = new ItemRequest(2L, "test description2", user1, dateTime);
        when(itemRequestStorage.findAllByRequester_IdNot(4L))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        Assertions.assertEquals(List.of(ItemRequestMapper.toDto(itemRequest1), ItemRequestMapper.toDto(itemRequest2)),
                itemRequestService.getAllRequests(user1.getId()));
        verify(itemRequestStorage).findAllByRequester_IdNot(4L);
    }

    @Test
    void testGetAllRequestsWithNotFoundUser() {
        when(userStorage.findById(4L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(4L));
    }

    @Test
    void testGetRequestById() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequest1 = new ItemRequest(1L, "test description", user1, dateTime);
        when(itemRequestStorage.findById(1L)).thenReturn(Optional.of(itemRequest1));
        User user2 = new User(2L, "testUser2", "test2@mail.ru");
        Item item1 = new Item(1L, "test1", "testD1", true, user2, itemRequest1);

        when(itemStorage.findAllByItemRequestId(itemRequest1.getId()))
                .thenReturn(List.of(item1));

        ItemRequestWithAnswerDto itemRequestWithAnswerDto = new ItemRequestWithAnswerDto(1L,
                "test description", user1.getId(), dateTime, List.of(ItemMapper
                .toItemAnswerRequestDto(item1)));
        Assertions.assertEquals(itemRequestWithAnswerDto, itemRequestService.getRequestById(1L,1L));
        verify(itemRequestStorage).findById(1L);
        verify(itemStorage).findAllByItemRequestId(itemRequest1.getId());
    }

    @Test
    void testGetRequestByIdWithNotFoundUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L,
                1L));
    }

    @Test
    void testGetRequestByIdWithNotFoundRequest() {
        User user1 = new User(1L, "testUser", "test@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRequestStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L,1L));
    }
}
