package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDescriptionDto itemRequestDescriptionDto);

    List<ItemRequestWithAnswerDto> getRequestUser(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestWithAnswerDto getRequestById(Long userId, Long requestId);
}
