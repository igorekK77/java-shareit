package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.TextCommentDto;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemCreateDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ValidationException("Имя должно быть указано!");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Описание должно быть указано!");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус должен быть указан!");
        }
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                             @RequestBody ItemDto newItemDto) {
        return itemClient.updateItem(userId, itemId, newItemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text) {
        return itemClient.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId, @RequestBody TextCommentDto text) {
        if (text.getText().isBlank()) {
            throw new ValidationException("Комментарий не должен быть путсым!");
        }
        return itemClient.createComment(userId, itemId, text);
    }
}
