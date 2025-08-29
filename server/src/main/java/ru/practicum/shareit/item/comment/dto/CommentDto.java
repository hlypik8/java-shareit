package ru.practicum.shareit.item.comment.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CommentDto {

    private Integer id;

    private String text;

    private ItemDto item;

    private String authorName;

    private LocalDateTime created;
}
