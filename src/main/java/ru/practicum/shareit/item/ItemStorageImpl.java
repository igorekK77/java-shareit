package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> itemRepository = new HashMap<>();
    private long itemCounter = 0;

    @Override
    public Item createItem(Item item) {
        item.setId(itemCounter);
        itemRepository.put(itemCounter, item);
        itemCounter++;
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item newItem) {
        if (!itemRepository.containsKey(itemId)) {
            throw new NotFoundException("Вещи с ID = " + itemId + " не существует!");
        }
        Item item = itemRepository.get(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("У пользователя с ID = " + userId + " нет доступа к изменению данной вещи!");
        }

        if (newItem.getName() != null && !newItem.getName().equals(item.getName())) {
            item.setName(newItem.getName());
        }

        if (newItem.getDescription() != null && !newItem.getDescription().equals(item.getDescription())) {
            item.setDescription(newItem.getDescription());
        }

        if (newItem.getAvailable() != item.getAvailable()) {
            item.setAvailable(newItem.getAvailable());
        }

        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        if (!itemRepository.containsKey(itemId)) {
            throw new NotFoundException("Вещи с ID = " + itemId + " не существует!");
        }
        return itemRepository.get(itemId);
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        return itemRepository.values().stream()
                .filter(itemDto -> itemDto.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Item> searchItem(String text) {
        String lowerText = text.toLowerCase();
        return itemRepository.values().stream()
                .filter(itemDto -> (itemDto.getName().toLowerCase().contains(lowerText) ||
                        itemDto.getDescription().toLowerCase().contains(lowerText)) &&
                        itemDto.getAvailable())
                .toList();
    }
}
