package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemAnswerRequestDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDescriptionDto itemRequestDescriptionDto) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        if (itemRequestDescriptionDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым!");
        }
        itemRequest.setDescription(itemRequestDescriptionDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toDto(itemRequestStorage.save(itemRequest));
    }

    @Override
    public List<ItemRequestWithAnswerDto> getRequestUser(Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));

        List<ItemRequest> allRequestsUser = itemRequestStorage.findAllByRequesterId(user.getId());
        if (allRequestsUser.isEmpty()) {
            throw new  ValidationException("Пользователь еще не создавал запросы!");
        }
        Map<Long, ItemRequest> requestsMap = new HashMap<>();
        List<Long> allRequestsIds = new ArrayList<>();
        for (ItemRequest itemRequest : allRequestsUser) {
            allRequestsIds.add(itemRequest.getId());
            requestsMap.put(itemRequest.getId(), itemRequest);
        }

        List<Item> allItemsAnswers = itemStorage.findAllByItemRequestIdIn(allRequestsIds);
        if (allItemsAnswers.isEmpty()) {
            return allRequestsUser.stream().map(ItemRequestMapper::toRequestWithAnswerDto).toList();
        }
        Map<Long, ItemRequestWithAnswerDto> itemRequestsWithAnswersMap = new HashMap<>();
        for (Item item : allItemsAnswers) {
            if (!itemRequestsWithAnswersMap.containsKey(item.getItemRequest().getId())) {
                ItemRequest itemRequest = requestsMap.get(item.getItemRequest().getId());
                List<ItemAnswerRequestDto> answer = new ArrayList<>();
                answer.add(ItemMapper.toItemAnswerRequestDto(item));
                itemRequestsWithAnswersMap.put(item.getItemRequest().getId(),
                        new ItemRequestWithAnswerDto(itemRequest.getId(), itemRequest.getDescription(),
                                itemRequest.getRequester().getId(), itemRequest.getCreated(), answer));
            } else {
                ItemRequestWithAnswerDto itemRequestWithAnswerDto = itemRequestsWithAnswersMap.get(item
                        .getItemRequest().getId());
                itemRequestWithAnswerDto.getItems().add(ItemMapper.toItemAnswerRequestDto(item));
            }
        }
        return itemRequestsWithAnswersMap.values().stream().sorted(Comparator
                .comparing(ItemRequestWithAnswerDto::getCreated).reversed()).toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));

        return itemRequestStorage.findAllByRequester_IdNot(user.getId()).stream()
                .map(ItemRequestMapper::toDto).sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
    }

    @Override
    public ItemRequestWithAnswerDto getRequestById(Long userId, Long requestId) {
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!"));

        ItemRequest itemRequest = itemRequestStorage.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с ID = " + requestId + " не найден!"));

        List<Item> itemsAnswer = itemStorage.findAllByItemRequestId(requestId);
        return new ItemRequestWithAnswerDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequester().getId(), itemRequest.getCreated(), itemsAnswer.stream()
                .map(ItemMapper::toItemAnswerRequestDto).toList());
    }

}
