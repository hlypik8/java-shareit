package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final String SHARER_ID_HEADER = "X-Sharer-User-Id";


    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemCreateDto itemCreateDto,
                           @RequestHeader(SHARER_ID_HEADER) Integer userId) {

        log.info("Добавление вещи пользователем с ID {}", userId);
        ItemDto createdItemDto = itemService.addItem(itemCreateDto, userId);
        log.debug("Вещь: {} успешно добавлена пользователю с ID: {}", createdItemDto.toString(), userId);
        return createdItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemUpdateDto itemUpdateDto,
                              @RequestHeader(SHARER_ID_HEADER) Integer userId,
                              @PathVariable Integer itemId) {
        log.info("Обновление вещи с ID: {} пользователем с ID {}", itemId, userId);
        ItemDto updatedItemDto = itemService.updateItem(itemUpdateDto, userId, itemId);
        log.debug("Вещь успешно обновлена: {}", updatedItemDto.toString());
        return updatedItemDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId) {
        log.info("Запрос вещи по ID: {}", itemId);
        ItemDto item = itemService.getItemById(itemId);
        log.debug("Получена вещь: {}", item);
        return item;
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(SHARER_ID_HEADER) Integer userId) {
        log.info("Запрос всех вещей пользователя с ID: {}", userId);
        List<ItemDto> items = itemService.getItemsByUserId(userId);
        log.debug("Найдено {} вещей для пользователя {}", items.size(), userId);
        return items;
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestParam(name = "text") String text) {
        log.info("Поиск вещей по тексту: '{}'", text);
        List<ItemDto> items = itemService.searchItems(text);
        log.debug("Найдено {} вещей по запросу '{}'", items.size(), text);
        return items;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(NotFoundException e) {
        log.warn("Ошибка при обработке запроса: {}", e.getMessage());
        return new ErrorResponse("Ошибка параметра", e.getMessage());
    }
}
