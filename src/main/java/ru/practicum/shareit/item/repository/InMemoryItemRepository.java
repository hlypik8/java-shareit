package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemCreateDto;
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
    public Item addItem(ItemCreateDto itemCreateDto, Integer ownerId) {
        Item item = itemDtoMapper.toItem(itemCreateDto, ownerId);
        int id = getNextId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(ItemUpdateDto itemUpdateDto, Integer itemId) {
        Item item = items.get(itemId);
        item.setName(itemUpdateDto.getName());
        item.setDescription(itemUpdateDto.getDescription());
        item.setAvailable(itemUpdateDto.getAvailable());
        item.setRequest(item.getRequest());
        return item;
    }

    @Override
    public Item getItemById(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUserId(Integer userId) {
        return items.values().stream()
                .filter(Objects::nonNull)
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isItemExists(Integer itemId) {
        return items.get(itemId) != null;
    }

    @Override
    public List<Item> searchItems(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String normalizedText = text.toLowerCase().trim();

        return items.values().stream()
                .filter(Objects::nonNull)
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> contains(item.getName(), normalizedText) ||
                        contains(item.getDescription(), normalizedText)
                )
                .collect(Collectors.toList());
    }

    private int getNextId() {
        return nextId++;
    }

    private boolean contains(String text, String search) {
        if (text == null || search == null) {
            return false;
        }
        return text.toLowerCase().contains(search);
    }
}
