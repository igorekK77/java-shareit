package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentRepository commentRepository;
    private final ItemRequestStorage itemRequestStorage;

    @Override
    public ItemDto createItem(Long userId, ItemCreateDto itemDto) {
        Item item = ItemMapper.toItemFromItemCreateDto(itemDto);
        item.setOwner(userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!")));
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestStorage.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Запрос с ID = " + itemDto.getRequestId() + " не найден!"));
            item.setItemRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с ID = " + itemId + " не найдена!"));
        if (newItemDto.getName() == null) {
            newItemDto.setName(item.getName());
        }
        if (newItemDto.getDescription() == null) {
            newItemDto.setDescription(item.getDescription());
        }
        if (newItemDto.getAvailable() == null) {
            newItemDto.setAvailable(item.getAvailable());
        }
        Item newItem = ItemMapper.toItemFromItemDto(newItemDto);
        newItem.setId(itemId);
        newItem.setOwner(user);
        return ItemMapper.toItemDto(itemStorage.save(newItem));
    }

    @Override
    public ItemDtoWithBookings getItemById(Long userId, Long itemId) {
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с ID = " + itemId + " не найдена!"));
        ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(item);
        List<Comment> itemComments = commentRepository.findAllByItemId(itemId);
        itemDtoWithBookings.setComments(itemComments);
        return itemDtoWithBookings;
    }

    @Override
    public List<ItemDtoWithBookings> getAllUserItems(Long userId) {
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));
        List<Booking> ownerListBooking = bookingStorage.findAllByItemOwnerIdOrderByStartDesc(userId);
        if (ownerListBooking.isEmpty()) {
            return itemStorage.findAllByOwnerId(userId).stream().map(ItemMapper::toItemDtoWithBookings)
                    .collect(Collectors.toList());
        }
        List<ItemDtoWithBookings> totalItemWithDates = new ArrayList<>();
        List<Long> usesItemId = new ArrayList<>();
        for (int i = 0; i < ownerListBooking.size(); i++) {
            ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(ownerListBooking.get(i)
                    .getItem());
            if (usesItemId.contains(itemDtoWithBookings.getId())) {
                continue;
            }
            Booking lastBooking = ownerListBooking.stream()
                    .filter(b -> b.getItem().getId().equals(itemDtoWithBookings.getId()))
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);
            Booking nextBooking = ownerListBooking.stream()
                    .filter(b -> b.getItem().getId().equals(itemDtoWithBookings.getId()))
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            if (lastBooking != null) {
                itemDtoWithBookings.setLastBooking(BookingMapper.toBookingDto(lastBooking));
            }
            if (nextBooking != null) {
                itemDtoWithBookings.setNextBooking(BookingMapper.toBookingDto(nextBooking));
            }
            totalItemWithDates.add(itemDtoWithBookings);
            usesItemId.add(itemDtoWithBookings.getId());
        }

        List<Comment> allCommentsForOwner = commentRepository.findAllByItemOwnerId(userId);
        Map<Long, List<Comment>> commentsWithIdItem = new HashMap<>();
        for (Comment comment: allCommentsForOwner) {
            if (!commentsWithIdItem.containsKey(comment.getItem().getId())) {
                List<Comment> newComment = new ArrayList<>();
                newComment.add(comment);
                commentsWithIdItem.put(comment.getItem().getId(), newComment);
            } else {
                List<Comment> comments = commentsWithIdItem.get(comment.getItem().getId());
                comments.add(comment);
            }
        }

        for (ItemDtoWithBookings itemDtoWithBookings : totalItemWithDates) {
            if (commentsWithIdItem.containsKey(itemDtoWithBookings.getId())) {
                itemDtoWithBookings.setComments(commentsWithIdItem.get(itemDtoWithBookings.getId()));
            }
        }

        return totalItemWithDates;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemStorage.findAllByAvailableAndNameContainingIgnoreCaseOrAvailableAndDescriptionContainingIgnoreCase(
                true, text, true, text).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, String text) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с ID = " + itemId + " не найдена!"));
        List<Booking> usersBooking = bookingStorage.findAllByBookerId(userId);
        boolean isCanUserAddComments = false;

        for (Booking booking: usersBooking) {
            if (booking.getItem().getId().equals(itemId)) {
                if (booking.getEnd().isBefore(LocalDateTime.now())) {
                    isCanUserAddComments = true;
                    break;
                }
            }
        }

        if (!isCanUserAddComments) {
            throw new ValidationException("Пользователь с ID = " + userId + " не может оставить отзыв для вещи с " +
                    "ID = " + itemId);
        }

        Comment comment = CommentMapper.createComment(text, user, item);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
