package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(Long userId, ItemCreateDto itemDto) {
        if (itemDto.getName().isEmpty()) {
            throw new ValidationException("Имя должно быть указано!");
        }
        if (itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Описание должно быть указано!");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус должен быть указан!");
        }
        Item item = ItemCreateDto.toItem(itemDto);
        item.setOwner(userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!")));
        return ItemDtoMapper.toItemDto(itemStorage.save(item));
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
        Item newItem = ItemDtoMapper.toItem(newItemDto);
        newItem.setId(itemId);
        newItem.setOwner(user);
        return ItemDtoMapper.toItemDto(itemStorage.save(newItem));
    }

    @Override
    public ItemDtoWithDates getItemById(Long userId, Long itemId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с ID = " + itemId + " не найдена!"));
        ItemDtoWithDates itemDtoWithDates = ItemDtoWithDates.toItemDtoWithDates(item);
        List<Comment> itemComments = commentRepository.findAllByItemId(itemId);
        itemDtoWithDates.setComments(itemComments);
        return itemDtoWithDates;
    }

    @Override
    public List<ItemDtoWithDates> getAllUserItems(Long userId) {
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));
        List<Booking> ownerListBooking = bookingStorage.findAllByItemOwnerId(userId);
        if (ownerListBooking.isEmpty()) {
            return itemStorage.findAllByOwnerId(userId).stream().map(ItemDtoWithDates::toItemDtoWithDates)
                    .collect(Collectors.toList());
        }
        Map<Long, ItemDtoWithDates> sortedItems = new HashMap<>();
        for (Booking booking: ownerListBooking) {
            if (!sortedItems.containsKey(booking.getItem().getId())) {
                ItemDtoWithDates itemDtoWithDates = ItemDtoWithDates.toItemDtoWithDates(booking.getItem());
                itemDtoWithDates.setLastBooking(booking.getStart());
                itemDtoWithDates.setEndLastBooking(booking.getEnd());
                sortedItems.put(booking.getItem().getId(), itemDtoWithDates);
            } else {
                ItemDtoWithDates itemDtoWithDates = sortedItems.get(booking.getItem().getId());
                if (itemDtoWithDates.getLastBooking().isAfter(booking.getStart()) &&
                        itemDtoWithDates.getNextBooking() == null) {
                    itemDtoWithDates.setNextBooking(itemDtoWithDates.getLastBooking());
                    itemDtoWithDates.setEndNextBooking(itemDtoWithDates.getEndLastBooking());
                    itemDtoWithDates.setLastBooking(booking.getStart());
                    itemDtoWithDates.setEndLastBooking(booking.getEnd());
                } else if (itemDtoWithDates.getLastBooking().isBefore(booking.getStart()) &&
                        itemDtoWithDates.getNextBooking() == null) {
                    itemDtoWithDates.setNextBooking(booking.getStart());
                    itemDtoWithDates.setEndNextBooking(booking.getEnd());
                } else if (itemDtoWithDates.getLastBooking().isBefore(booking.getStart())) {
                    if (itemDtoWithDates.getNextBooking().isBefore(booking.getStart())) {
                        itemDtoWithDates.setLastBooking(itemDtoWithDates.getNextBooking());
                        itemDtoWithDates.setEndLastBooking(itemDtoWithDates.getEndNextBooking());
                        itemDtoWithDates.setNextBooking(booking.getStart());
                        itemDtoWithDates.setEndNextBooking(booking.getEnd());
                    } else if (itemDtoWithDates.getNextBooking().isAfter(booking.getStart())) {
                        itemDtoWithDates.setLastBooking(booking.getStart());
                        itemDtoWithDates.setEndLastBooking(booking.getEnd());
                    }
                }
                sortedItems.put(booking.getItem().getId(), itemDtoWithDates);
            }
        }
        List<ItemDtoWithDates> totalItemWithDates = sortedItems.values().stream().toList();
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

        for (ItemDtoWithDates itemDtoWithDates: totalItemWithDates) {
            if (commentsWithIdItem.containsKey(itemDtoWithDates.getId())) {
                itemDtoWithDates.setComments(commentsWithIdItem.get(itemDtoWithDates.getId()));
            }
        }

        return totalItemWithDates;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemStorage.findAllByAvailableAndNameContainingIgnoreCaseOrAvailableAndDescriptionContainingIgnoreCase(
                true, text, true, text).stream().map(ItemDtoMapper::toItemDto).toList();
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
                    LocalDateTime time = LocalDateTime.now();
                    isCanUserAddComments = true;
                    break;
                }
            }
        }

        if (!isCanUserAddComments) {
            throw new ValidationException("Пользователь с ID = " + userId + " не может оставить отзыв для вещи с " +
                    "ID = " + itemId);
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());
        comment.setUser(user);
        comment.setItem(item);
        return CommentDto.toCommentDto(commentRepository.save(comment));
    }
}
