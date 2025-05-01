package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    ItemDto createItem(Item item);

    ItemDto updateItem(Long userId, Long itemId, Item newItem);

    Item getItemById(Long itemId);

    List<Item> getAllUserItems(Long userId);

    List<Item> searchItem(String text);
}
