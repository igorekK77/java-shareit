package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemCreateDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDto newItemDto) {
        return itemService.updateItem(userId, itemId, newItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithDates getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoWithDates> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId, @RequestBody TextCommentDto text) {
        if (text.getText().isBlank()) {
            throw new ValidationException("Комментарий не должен быть путсым!");
        }
        return itemService.createComment(userId, itemId, text.getText());
    }
}
