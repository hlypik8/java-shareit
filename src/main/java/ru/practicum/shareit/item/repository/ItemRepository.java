package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;


public interface ItemRepository {

    Item addItem(ItemCreateDto itemCreateDto, Integer ownerId);

    Item getItemById(Integer itemId);

    Item updateItem(ItemUpdateDto itemUpdateDto, Integer itemId);

    List<Item> getItemsByUserId(Integer userId);

    boolean isItemExists(Integer itemId);

    List<Item> searchItems(String text);

}
