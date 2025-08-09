package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.InvalidItemOwnerException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemDtoMapper itemDtoMapper;

    public ItemDto addItem(ItemCreateDto itemCreateDto, Integer ownerId) throws NotFoundException {
        log.info("Добавление вещи пользователем с ID {}", ownerId);

        if (!userService.isUserExists(ownerId)) {
            log.warn("Пользователь с ID: {} не найден", ownerId);
            throw new NotFoundException("Пользователь с ID: " + ownerId + " не найден");
        }

        ItemDto createdItem = itemDtoMapper.toDto(itemRepository.addItem(itemCreateDto, ownerId));

        log.debug("Вещь: {} успешно добавлена пользователю с ID: {}", createdItem.toString(), ownerId);

        return createdItem;
    }

    public ItemDto updateItem(ItemUpdateDto itemUpdateDto,
                              Integer userId,
                              Integer itemId) throws InvalidItemOwnerException, NotFoundException {
        log.info("Обновление вещи с ID: {} пользователем с ID {}", itemId, userId);
        if (!isItemExists(itemId)) {
            log.warn("Вещь с ID: {} не найдена", itemId);
            throw new NotFoundException("Вещь с ID: " + itemId + " не найдена");
        }
        if (!userService.isUserExists(userId)) {
            log.warn("Пользователь с ID: {} не найден", userId);
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        if (!itemRepository.getItemById(itemId).getOwner().equals(userId)) {
            log.warn("Владелец вещи c ID: {} и пользователь не совпадают", userId);
            throw new InvalidItemOwnerException("Владелец вещи и пользователь не совпадают");
        }

        ItemDto updatedItem = itemDtoMapper.toDto(itemRepository.updateItem(itemUpdateDto, itemId));

        log.debug("Вещь успешно обновлена: {}", updatedItem.toString());

        return updatedItem;
    }

    public List<ItemDto> getItemsByUserId(Integer userId) throws NotFoundException {
        log.info("Запрос всех вещей пользователя с ID: {}", userId);

        if (!userService.isUserExists(userId)) {
            log.warn("Пользователь с ID: {} не найден", userId);
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        List<ItemDto> items = itemRepository.getItemsByUserId(userId).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());

        log.debug("Найдено {} вещей для пользователя {}", items.size(), userId);

        return items;
    }

    public ItemDto getItemById(Integer itemId) throws NotFoundException {
        log.info("Запрос вещи по ID: {}", itemId);

        if (!isItemExists(itemId)) {
            log.warn("Вещь с ID: {} не найдена", itemId);
            throw new NotFoundException("Вещь с ID: " + itemId + " не найдена");
        }

        ItemDto item = itemDtoMapper.toDto(itemRepository.getItemById(itemId));

        log.debug("Получена вещь: {}", item);

        return item;
    }

    public List<ItemDto> searchItems(String text) {
        log.info("Поиск вещей по тексту: '{}'", text);

        List<ItemDto> items = itemRepository.searchItems(text).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());

        log.debug("Найдено {} вещей по запросу '{}'", items.size(), text);

        return items;
    }

    public boolean isItemExists(Integer itemId) {
        return itemRepository.isItemExists(itemId);
    }
}
