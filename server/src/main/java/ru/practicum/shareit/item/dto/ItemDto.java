package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private Integer requestId;
}
