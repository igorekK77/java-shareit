package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private ItemRequestStorage itemRequestStorage;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testCreateItem() {
        ItemRequest itemRequest = new ItemRequest(1L, "testRequest", new User(2L,
                "testUser2", "test2@mail.ru"), LocalDateTime.now());
        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", true,
                1L);
        Item itemWithOutId = new Item(null,"test1", "testDescription1", true,
                new User(1L, "testUser", "test@mail.ru"), itemRequest);
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), itemRequest);
        when(itemStorage.save(itemWithOutId)).thenReturn(item);
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(itemRequestStorage.findById(1L)).thenReturn(Optional.of(itemRequest));
        Assertions.assertEquals(ItemMapper.toItemDto(item), itemService.createItem(1L, itemCreateDto));
        verify(itemStorage, times(1)).save(itemWithOutId);
    }

    @Test
    void testCreateItemNotFoundUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", true,
                null);
        Assertions.assertThrows(NotFoundException.class, () -> itemService.createItem(1L, itemCreateDto));
        verify(userStorage, times(1)).findById(1L);
    }

    @Test
    void testCreateItemNotFoundRequest() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", true,
                1L);
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(itemRequestStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.createItem(1L, itemCreateDto));
        verify(userStorage, times(1)).findById(1L);
    }

    @Test
    void testUpdateItem() {
        ItemDto newItemDto = new ItemDto(1L, "newTest1", "newTest1D", true, 1L,
                null);
        Item item = new Item(1L, "newTest1", "newTest1D", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(itemStorage.save(item)).thenReturn(item);
        Assertions.assertEquals(newItemDto, itemService.updateItem(1L, 1L, newItemDto));
        verify(itemStorage, times(1)).save(item);
    }

    @Test
    void testUpdateItemNotFoundUser() {
        ItemDto newItemDto = new ItemDto(1L, "newTest1", "newTest1D", true, 1L,
                null);
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 1L, newItemDto));
        verify(userStorage, times(1)).findById(1L);
    }

    @Test
    void testUpdateItemWithNewItemParamsNull() {
        ItemDto newItemDto = new ItemDto(1L, null, null, null, 1L,
                null);
        Item item = new Item(1L, "newTest1", "newTest1D", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(itemStorage.save(item)).thenReturn(item);
        Assertions.assertEquals(newItemDto, itemService.updateItem(1L, 1L, newItemDto));
        verify(itemStorage, times(1)).save(item);
    }

    @Test
    void testUpdateItemNotFoundItem() {
        ItemDto newItemDto = new ItemDto(1L, "newTest1", "newTest1D", true, 1L,
                null);
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(itemStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 1L, newItemDto));
        verify(userStorage, times(1)).findById(1L);
    }

    @Test
    void testGetItemById() {
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(1L, "test1",
                "testDescription1", true, new User(1L, "testUser", "test@mail.ru"),
                null, null, List.of(new Comment()));
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(new Comment()));
        Assertions.assertEquals(itemDtoWithBookings, itemService.getItemById(1L, 1L));
        verify(itemStorage, times(1)).findById(1L);
    }

    @Test
    void testGetItemByIdWithNotFoundUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));
        verify(userStorage, times(1)).findById(1L);
    }

    @Test
    void testGetItemByIdWithNotFoundItems() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(itemStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));
        verify(userStorage, times(1)).findById(1L);
    }

    @Test
    void testGetAllUserItems() {
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                new Item(1L, "test1", "testDescription1", true, new User(3L,
                        "test3", "test@mail.ru"), null), new User(1L,
                "testUser", "test@mail.ru"), BookingStatus.CANCELED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                new Item(1L, "test1", "testDescription1", true, new User(3L,
                        "test3", "test@mail.ru"), null), new User(2L,
                "testUse2", "test2@mail.ru"), BookingStatus.APPROVED);
        Comment comment1 = new Comment(1L, "testComment", new Item(1L, "test1",
                "testDescription1", true, new User(3L, "test3", "test@mail.ru"),
                null), new User(1L, "testUser", "test@mail.ru"), LocalDateTime.now());
        Comment comment2 = new Comment(2L, "testComment2", new Item(1L, "test1",
                "testDescription1", true, new User(3L, "test3", "test@mail.ru"),
                null), new User(1L, "testUser", "test@mail.ru"),
                LocalDateTime.now().plusMinutes(10));
        ItemDtoWithBookings itemDtoWithBookings1 = new ItemDtoWithBookings(1L, "test1",
                "testDescription1", true, new User(3L, "test3", "test@mail.ru"),
                BookingMapper.toBookingDto(booking1), BookingMapper.toBookingDto(booking2), List.of(comment1,
                comment2));
        when(userStorage.findById(3L)).thenReturn(Optional.of(new User(3L, "test3", "test@mail.ru")));
        when(bookingStorage.findAllByItemOwnerIdOrderByStartDesc(3L)).thenReturn(List.of(booking1, booking2));
        when(commentRepository.findAllByItemOwnerId(3L)).thenReturn(List.of(comment1, comment2));
        Assertions.assertEquals(List.of(itemDtoWithBookings1), itemService.getAllUserItems(3L));
    }

    @Test
    void testGetAllUserItemsWithNotFoundUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getAllUserItems(1L));
    }

    @Test
    void testGetAllUserItemsWithNotFoundOwnerListBooking() {
        ItemDtoWithBookings itemDtoWithBookings1 = new ItemDtoWithBookings(1L, "test1",
                "testDescription1", true, new User(3L, "test3", "test@mail.ru"),
                null, null, null);
        Item item = new Item(1L, "test1", "testDescription1", true, new User(3L,
                "test3", "test@mail.ru"), null);
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(bookingStorage.findAllByItemOwnerIdOrderByStartDesc(1L)).thenReturn(List.of());
        when(itemStorage.findAllByOwnerId(1L)).thenReturn(List.of(item));
        Assertions.assertEquals(List.of(itemDtoWithBookings1), itemService.getAllUserItems(1L));
    }

    @Test
    void testSearchItems() {
        ItemDto itemDto1 = new ItemDto(1L, "testSearch1", "test1D", true, 1L,
                null);
        ItemDto itemDto2 = new ItemDto(2L, "test1", "testSearch1D", true, 2L,
                null);
        Item item1 = new Item(1L, "testSearch1", "test1D", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Item item2 = new Item(2L, "test1", "testSearch1D", true, new User(2L,
                "testUser2", "test2@mail.ru"), null);
        when(itemStorage
                .findAllByAvailableAndNameContainingIgnoreCaseOrAvailableAndDescriptionContainingIgnoreCase(
                        true, "testSearch", true, "testSearch")).thenReturn(List
                .of(item1, item2));
        Assertions.assertEquals(List.of(itemDto1, itemDto2), itemService.searchItem("testSearch"));
    }

    @Test
    void testCreateComment() {
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                new Item(1L, "test1", "testDescription1", true, new User(3L,
                        "test3", "test@mail.ru"), null), new User(1L,
                "testUser", "test@mail.ru"), BookingStatus.CANCELED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                new Item(1L, "test1", "testDescription1", true, new User(3L,
                        "test3", "test@mail.ru"), null), new User(2L,
                "testUse2", "test2@mail.ru"), BookingStatus.APPROVED);
        Comment comment = new Comment(1L, "testComment", new Item(1L, "test1",
                "testDescription1", true, new User(3L, "test3", "test@mail.ru"),
                null), new User(1L, "testUser", "test@mail.ru"), LocalDateTime.now());
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(bookingStorage.findAllByBookerId(1L)).thenReturn(List.of(booking1, booking2));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Assertions.assertEquals(CommentMapper.toCommentDto(comment), itemService.createComment(1L, 1L,
                "testComment"));
        verify(userStorage, times(1)).findById(1L);
    }

    @Test
    void testCreateCommentWithNotFoundUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.createComment(1L, 1L,
                "testComment"));
    }

    @Test
    void testCreateCommentWithNotFoundItem() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(itemStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.createComment(1L, 1L,
                "testComment"));
    }

    @Test
    void testCreateCommentWithNotUserBookItem() {
        Item item = new Item(1L, "test1", "testDescription1", true, new User(1L,
                "testUser", "test@mail.ru"), null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                new Item(11L, "test1", "testDescription1", true, new User(3L,
                        "test3", "test@mail.ru"), null), new User(1L,
                "testUser", "test@mail.ru"), BookingStatus.CANCELED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                new Item(13L, "test1", "testDescription1", true, new User(3L,
                        "test3", "test@mail.ru"), null), new User(1L,
                "testUse2", "test2@mail.ru"), BookingStatus.APPROVED);
        when(userStorage.findById(1L)).thenReturn(Optional.of(new User(1L, "testUser", "test@mail.ru")));
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(bookingStorage.findAllByBookerId(1L)).thenReturn(List.of(booking1, booking2));

        Assertions.assertThrows(ValidationException.class, () -> itemService.createComment(1L, 1L,
                "testComment"));
    }

}
