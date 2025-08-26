package ru.practicum.shareit.item.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.User;

@UtilityClass
public class CommentDtoMapper {


    public Comment toComment(CommentCreateDto commentCreateDto, Item item, User user) {
        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                ItemDtoMapper.toDto(comment.getItem()),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
