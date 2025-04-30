package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        userService.getUserById(userId);
        if (itemDto.getName().isEmpty()) {
            throw new ValidationException("Имя должно быть указано!");
        }
        if (itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Описание должно быть указано!");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус должен быть указан!");
        }
        return itemStorage.createItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto) {
        userService.getUserById(userId);
        return itemStorage.updateItem(userId, itemId, newItemDto);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemStorage.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        userService.getUserById(userId);
        return itemStorage.getAllUserItems(userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemStorage.searchItem(text);
    }
}
