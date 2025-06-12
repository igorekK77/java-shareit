package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getRequester().getId(),
                itemRequest.getCreated());
    }

    public static ItemRequestWithAnswerDto toRequestWithAnswerDto(ItemRequest itemRequest) {
        ItemRequestWithAnswerDto requestWithAnswerDto = new ItemRequestWithAnswerDto();
        requestWithAnswerDto.setId(itemRequest.getId());
        requestWithAnswerDto.setDescription(itemRequest.getDescription());
        requestWithAnswerDto.setCreated(itemRequest.getCreated());
        requestWithAnswerDto.setUserId(itemRequest.getRequester().getId());
        return requestWithAnswerDto;
    }
}
