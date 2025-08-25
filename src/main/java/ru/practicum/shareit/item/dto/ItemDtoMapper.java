package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemDtoMapper {

    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest());
    }

    public Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest());
    }

    public Item toItem(ItemCreateDto itemCreateDto, Integer ownerId) {
        return new Item(null,
                itemCreateDto.getName(),
                itemCreateDto.getDescription(),
                itemCreateDto.getAvailable(),
                ownerId,
                null);
    }

    public ItemWithBookingAndCommentsDto toItemWithBookingAndCommentsDto(Item item,
                                                                         Booking lastBooking,
                                                                         Booking nextBooking,
                                                                         List<Comment> comments) {
        return new ItemWithBookingAndCommentsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                null,
                lastBooking != null ? BookingDtoMapper.toBookingDto(lastBooking) : null,
                nextBooking != null ? BookingDtoMapper.toBookingDto(nextBooking) : null,
                comments.stream().map(CommentDtoMapper::toCommentDto).collect(Collectors.toList())
        );
    }

    public Item updateItemFields(Item item, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto.hasName()) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.hasDescription()) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.hasAvailable()) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }

        return item;
    }
}
