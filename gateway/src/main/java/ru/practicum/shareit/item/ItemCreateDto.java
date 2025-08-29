package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ItemCreateDto {

    @NotBlank(message = "Имя не может быть пустым")
    String name;
    @NotBlank(message = "Описание не может быть пустым")
    String description;
    @NotNull(message = "Статус доступности не может быть пустой")
    Boolean available;
}
