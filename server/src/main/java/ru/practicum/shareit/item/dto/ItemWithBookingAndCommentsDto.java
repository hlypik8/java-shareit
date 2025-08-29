package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.request.Request;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingAndCommentsDto {

    Integer id;
    String name;
    String description;
    Boolean available;
    Integer owner;
    Request request;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
}
