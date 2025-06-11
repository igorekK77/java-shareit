package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemCreateDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto);

    ItemDtoWithBookings getItemById(Long userId, Long itemId);

    List<ItemDtoWithBookings> getAllUserItems(Long userId);

    List<ItemDto> searchItem(String text);

    CommentDto createComment(Long userId, Long itemId, String text);
}
