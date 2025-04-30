package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, ItemDto> itemRepository = new HashMap<>();
    private long itemCounter = 0;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        itemDto.setOwnerId(userId);
        itemDto.setId(itemCounter);
        itemRepository.put(itemCounter, itemDto);
        itemCounter++;
        return itemDto;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto) {
        if (!itemRepository.containsKey(itemId)) {
            throw new NotFoundException("Вещи с ID = " + itemId + " не существует!");
        }
        ItemDto itemDto = itemRepository.get(itemId);
        if (!itemDto.getOwnerId().equals(userId)) {
            throw new ValidationException("У пользователя с ID = " + userId + " нет доступа к изменению данной вещи!");
        }

        if (newItemDto.getName() != null && !newItemDto.getName().equals(itemDto.getName())) {
            itemDto.setName(newItemDto.getName());
        }

        if (newItemDto.getDescription() != null && !newItemDto.getDescription().equals(itemDto.getDescription())) {
            itemDto.setDescription(newItemDto.getDescription());
        }

        if (newItemDto.getAvailable() != itemDto.getAvailable()) {
            itemDto.setAvailable(newItemDto.getAvailable());
        }

        return itemDto;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        if (!itemRepository.containsKey(itemId)) {
            throw new NotFoundException("Вещи с ID = " + itemId + " не существует!");
        }
        return itemRepository.get(itemId);
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        return itemRepository.values().stream()
                .filter(itemDto -> itemDto.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        String lowerText = text.toLowerCase();
        return itemRepository.values().stream()
                .filter(itemDto -> (itemDto.getName().toLowerCase().contains(lowerText) ||
                        itemDto.getDescription().toLowerCase().contains(lowerText)) &&
                        itemDto.getAvailable())
                .toList();
    }
}
