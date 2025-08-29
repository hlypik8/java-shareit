package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.error.exceptions.BookingNotvalidException;
import ru.practicum.shareit.error.exceptions.InvalidItemOwnerException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final String sharerIdHeader = "X-Sharer-User-Id";


    @PostMapping
    public ItemDto addItem(@RequestBody ItemCreateDto itemCreateDto,
                           @RequestHeader(sharerIdHeader) Integer userId) throws NotFoundException {
        return itemService.addItem(itemCreateDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemUpdateDto itemUpdateDto,
                              @RequestHeader(sharerIdHeader) Integer userId,
                              @PathVariable Integer itemId) throws InvalidItemOwnerException, NotFoundException {
        return itemService.updateItem(itemUpdateDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingAndCommentsDto getItemById(@PathVariable Integer itemId) throws NotFoundException {
        return itemService.getItemDtoById(itemId);
    }

    @GetMapping
    public List<ItemWithBookingAndCommentsDto> getItemsByUserId(@RequestHeader(sharerIdHeader) Integer userId) throws NotFoundException {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestParam(name = "text") String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Integer itemId,
                                 @RequestBody CommentCreateDto commentCreateDto,
                                 @RequestHeader(sharerIdHeader) Integer userId) throws NotFoundException, BookingNotvalidException {
        return itemService.createComment(commentCreateDto, itemId, userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(NotFoundException e) {
        return new ErrorResponse("Ошибка параметра", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(BookingNotvalidException e) {
        return new ErrorResponse("Ошибка параметра", e.getMessage());
    }
}
