package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestDescriptionDto itemRequestDescriptionDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDescriptionDto);
    }

    @GetMapping
    public List<ItemRequestWithAnswerDto> getRequestUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswerDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }

}
