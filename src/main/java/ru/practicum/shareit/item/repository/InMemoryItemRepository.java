package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final ItemDtoMapper itemDtoMapper;

    private final Map<Integer, Item> items = new HashMap<>();
    private int nextId = 0;

    @Override
    public ItemDto addItem(ItemCreateDto itemCreateDto, Integer ownerId) {
        Item item = itemDtoMapper.toItem(itemCreateDto, ownerId);
        int id = getNextId();
        item.setId(id);
        items.put(id, item);
        return itemDtoMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto itemUpdateDto, Integer itemId) {
        Item item = items.get(itemId);
        item.setName(itemUpdateDto.getName());
        item.setDescription(itemUpdateDto.getDescription());
        item.setAvailable(itemUpdateDto.getAvailable());
        item.setRequest(item.getRequest());
        return itemDtoMapper.toDto(item);
    }

    @Override
    public Optional<Item> getItemById(Integer itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<ItemDto> getAllItems() {
        return items.values().stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    private int getNextId() {
        return nextId++;
    }
}
