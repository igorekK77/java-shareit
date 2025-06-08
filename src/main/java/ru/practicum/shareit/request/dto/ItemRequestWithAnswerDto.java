package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemAnswerRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestWithAnswerDto {
    private Long id;

    private String description;

    private Long userId;

    private LocalDateTime created;

    private List<ItemAnswerRequestDto> answers;
}
