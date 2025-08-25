package ru.practicum.shareit.item.comment;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentDto {
    private Integer id;

    private String text;

    private Integer itemId;

    private Integer authorId;
}
