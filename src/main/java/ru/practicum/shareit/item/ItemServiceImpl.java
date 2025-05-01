package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        if (itemDto.getName().isEmpty()) {
            throw new ValidationException("Имя должно быть указано!");
        }
        if (itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Описание должно быть указано!");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус должен быть указан!");
        }
        Item item = ItemDtoMapper.toItem(itemDto);
        item.setOwner(userStorage.getUserById(userId));
        return itemStorage.createItem(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto) {
        Item newItem = ItemDtoMapper.toItem(newItemDto);
        newItem.setOwner(userStorage.getUserById(userId));
        return itemStorage.updateItem(userId, itemId, newItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemDtoMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        userStorage.getUserById(userId);
        return itemStorage.getAllUserItems(userId).stream().map(ItemDtoMapper::toItemDto).toList();
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemStorage.searchItem(text).stream().map(ItemDtoMapper::toItemDto).toList();
    }
}
