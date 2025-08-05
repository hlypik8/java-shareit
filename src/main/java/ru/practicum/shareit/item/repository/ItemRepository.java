package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Optional;


public interface ItemRepository {

    ItemDto addItem(ItemCreateDto itemCreateDto, Integer ownerId);

    Optional<Item> getItemById(Integer itemId);

    ItemDto updateItem(ItemUpdateDto itemUpdateDto, Integer itemId);
}
