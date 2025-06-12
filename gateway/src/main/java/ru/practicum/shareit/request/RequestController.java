package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody ItemRequestDescriptionDto itemRequestDescriptionDto) {
        if (itemRequestDescriptionDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым!");
        }
        return requestClient.createRequest(userId, itemRequestDescriptionDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getRequestUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        return requestClient.getRequest(userId, requestId);
    }
}
