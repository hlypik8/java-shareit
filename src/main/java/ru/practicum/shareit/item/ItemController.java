package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final String SHARER_ID_HEADER = "X-Sharer-User-Id";


    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemCreateDto itemCreateDto,
                           @RequestHeader(SHARER_ID_HEADER) Integer userId){
        return itemService.addItem(itemCreateDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemUpdateDto itemUpdateDto,
                              @RequestHeader(SHARER_ID_HEADER) Integer userId,
                              @PathVariable Integer itemId){
        return itemService.updateItem(itemUpdateDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId){
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(SHARER_ID_HEADER) Integer userId){
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestParam(name = "text") String text){
        return itemService.searchItems(text);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(NotFoundException e) {
        return new ErrorResponse("Ошибка параметра", e.getMessage());
    }
}
