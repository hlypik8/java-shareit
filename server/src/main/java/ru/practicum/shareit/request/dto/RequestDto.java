package ru.practicum.shareit.request.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class RequestDto {

    private Integer id;

    private String description;

    private Integer requester;

    private LocalDateTime created;

    private List<ItemDto> items;
}
