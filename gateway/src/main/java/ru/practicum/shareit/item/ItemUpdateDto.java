package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemUpdateDto {

    String name;
    String description;
    Boolean available;

    public boolean hasName() {
        return name != null && !name.isEmpty();
    }

    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    public boolean hasAvailable() {
        return available != null;
    }
}
