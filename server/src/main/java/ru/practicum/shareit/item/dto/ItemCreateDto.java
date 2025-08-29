package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@NoArgsConstructor
public class ItemCreateDto {

    String name;
    String description;
    Boolean available;
    Integer requestId;
}
