package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    private final String sharerIdHeader = "X-Sharer-User-Id";


    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemCreateDto itemCreateDto,
                                          @RequestHeader(sharerIdHeader) Integer userId) {
        return itemClient.addItem(itemCreateDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Valid ItemUpdateDto itemUpdateDto,
                              @RequestHeader(sharerIdHeader) Integer userId,
                              @PathVariable Integer itemId) {
        return itemClient.updateItem(itemUpdateDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Integer itemId) {
        return itemClient.getItemDtoById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(sharerIdHeader) Integer userId) {
        return itemClient.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearch(@RequestParam(name = "text") String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Integer itemId,
                                 @RequestBody @Valid CommentCreateDto commentCreateDto,
                                 @RequestHeader(sharerIdHeader) Integer userId) {
        return itemClient.createComment(commentCreateDto, itemId, userId);
    }
}
