package ru.practicum.shareit.item.comment.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.comment.Comment;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentDtoMapperTest {

    @Test
    void toComment_shouldMapFieldsCorrectly() {
        CommentCreateDto dto = new CommentCreateDto("Great item!");
        Item item = new Item(1, "Drill", "Powerful drill", true, 100, new Request());
        User user = new User(5, "Alice", "alice@mail.com");

        Comment comment = CommentDtoMapper.toComment(dto, item, user);

        assertNotNull(comment);
        assertEquals("Great item!", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(user, comment.getAuthor());
    }

    @Test
    void toCommentDto_shouldMapFieldsCorrectly() {
        User user = new User(5, "Bob", "bob@mail.com");
        Item item = new Item(2, "Молоток", "Тяжелый", true, 200, null);

        Comment comment = new Comment();
        comment.setId(10);
        comment.setText("Полезный инструмент");
        comment.setItem(item);
        comment.setAuthor(user);
        LocalDateTime created = LocalDateTime.now();
        comment.setCreated(created);

        CommentDto dto = CommentDtoMapper.toCommentDto(comment);

        assertNotNull(dto);
        assertEquals(10, dto.getId());
        assertEquals("Полезный инструмент", dto.getText());
        assertEquals("Bob", dto.getAuthorName());
        assertEquals(created, dto.getCreated());

        ItemDto itemDto = dto.getItem();
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getOwner(), itemDto.getOwner());
    }
}