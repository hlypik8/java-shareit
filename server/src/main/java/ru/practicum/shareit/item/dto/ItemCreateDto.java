package ru.practicum.shareit.item.dto;

import lombok.Getter;

@Getter
public class ItemCreateDto {

    String name;
    String description;
    Boolean available;
}
