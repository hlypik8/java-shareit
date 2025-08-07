package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.InvalidItemOwnerException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemDtoMapper itemDtoMapper;

    public ItemDto addItem(ItemCreateDto itemCreateDto, Integer ownerId) {
        userService.isUserExists(ownerId);
        return itemRepository.addItem(itemCreateDto, ownerId);
    }

    public ItemDto updateItem(ItemUpdateDto itemUpdateDto, Integer userId, Integer itemId) {
        isItemExists(itemId);
        userService.isUserExists(userId);
        if (!itemRepository.getItemById(itemId).get().getOwner().equals(userId)) {
            throw new InvalidItemOwnerException("Владелец вещи и пользователь не совпадают");
        }
        return itemRepository.updateItem(itemUpdateDto, itemId);
    }

    public List<ItemDto> getItemsByUserId(Integer userId) {
        return itemRepository.getAllItems().stream()
                .filter(Objects::nonNull)
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(Integer itemId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        return itemDtoMapper.toDto(item);
    }

    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String normalizedText = text.toLowerCase().trim();

        return itemRepository.getAllItems().stream()
                .filter(Objects::nonNull)
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item ->
                        contains(item.getName(), normalizedText) ||
                                contains(item.getDescription(), normalizedText)
                )
                .collect(Collectors.toList());
    }

    public void isItemExists(Integer itemId) {
        getItemById(itemId);
    }

    private boolean contains(String text, String search) {
        if (text == null || search == null) {
            return false;
        }
        return text.toLowerCase().contains(search);
    }
}
