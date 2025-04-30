package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllUserItems(Long userId);

    List<ItemDto> searchItem(String text);
}
