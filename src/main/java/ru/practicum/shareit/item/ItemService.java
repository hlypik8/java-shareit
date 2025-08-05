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

    public ItemDto getItemById(Integer itemId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        return itemDtoMapper.toDto(item);
    }

    public void isItemExists(Integer itemId) {
        getItemById(itemId);
    }
}
