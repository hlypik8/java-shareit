package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.request.Request;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemDtoMapperTest {

    @Test
    void toDto_whenRequestIsNull() {
        Item item = new Item();
        item.setId(10);
        item.setName("Дрель");
        item.setDescription("Электрическая");
        item.setAvailable(true);
        item.setOwner(5);
        item.setRequest(null);

        ItemDto dto = ItemDtoMapper.toDto(item);

        assertEquals(10, dto.getId());
        assertEquals("Дрель", dto.getName());
        assertEquals("Электрическая", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(5, dto.getOwner());
        assertNull(dto.getRequestId(), "requestId должен быть null когда request == null");
    }

    @Test
    void toDto_whenRequestPresent() {
        Request request = new Request();
        request.setId(77);

        Item item = new Item();
        item.setId(11);
        item.setName("Пила");
        item.setDescription("Ручная");
        item.setAvailable(false);
        item.setOwner(6);
        item.setRequest(request);

        ItemDto dto = ItemDtoMapper.toDto(item);

        assertEquals(11, dto.getId());
        assertEquals("Пила", dto.getName());
        assertEquals("Ручная", dto.getDescription());
        assertFalse(dto.getAvailable());
        assertEquals(6, dto.getOwner());
        assertEquals(77, dto.getRequestId());
    }

    @Test
    void toItem_createsItemFromCreateDto() {
        ItemCreateDto createDto = mock(ItemCreateDto.class);
        when(createDto.getName()).thenReturn("Молоток");
        when(createDto.getDescription()).thenReturn("Тяжелый");
        when(createDto.getAvailable()).thenReturn(true);

        Request request = new Request();
        request.setId(123);

        Item item = ItemDtoMapper.toItem(createDto, 9, request);

        assertNull(item.getId(), "id должен быть null при создании через toItem");
        assertEquals("Молоток", item.getName());
        assertEquals("Тяжелый", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(9, item.getOwner());
        assertSame(request, item.getRequest());
    }

    @Test
    void toItemWithBookingAndCommentsDto() {
        Request request = new Request();
        request.setId(5);

        Item item = new Item();
        item.setId(101);
        item.setName("Камера");
        item.setDescription("Цифровая");
        item.setAvailable(true);
        item.setOwner(20);
        item.setRequest(request);

        Booking lastBooking = mock(Booking.class);
        Booking nextBooking = mock(Booking.class);

        Comment comment1 = mock(Comment.class);
        Comment comment2 = mock(Comment.class);
        List<Comment> comments = List.of(comment1, comment2);

        BookingDto lastBookingDto = mock(BookingDto.class);
        BookingDto nextBookingDto = mock(BookingDto.class);

        CommentDto commentDto1 = mock(CommentDto.class);
        CommentDto commentDto2 = mock(CommentDto.class);

        try (MockedStatic<BookingDtoMapper> bookingMapper = Mockito.mockStatic(BookingDtoMapper.class);
             MockedStatic<CommentDtoMapper> commentMapper = Mockito.mockStatic(CommentDtoMapper.class)) {

            bookingMapper.when(() -> BookingDtoMapper.toBookingDto(lastBooking)).thenReturn(lastBookingDto);
            bookingMapper.when(() -> BookingDtoMapper.toBookingDto(nextBooking)).thenReturn(nextBookingDto);

            commentMapper.when(() -> CommentDtoMapper.toCommentDto(comment1)).thenReturn(commentDto1);
            commentMapper.when(() -> CommentDtoMapper.toCommentDto(comment2)).thenReturn(commentDto2);

            ItemWithBookingAndCommentsDto result =
                    ItemDtoMapper.toItemWithBookingAndCommentsDto(item, lastBooking, nextBooking, comments);

            assertEquals(101, result.getId());
            assertEquals("Камера", result.getName());
            assertEquals("Цифровая", result.getDescription());
            assertTrue(result.getAvailable());
            assertEquals(20, result.getOwner());
            assertSame(request, result.getRequest());

            assertSame(lastBookingDto, result.getLastBooking());
            assertSame(nextBookingDto, result.getNextBooking());

            List<CommentDto> commentDtos = result.getComments();
            assertEquals(2, commentDtos.size());
            assertSame(commentDto1, commentDtos.get(0));
            assertSame(commentDto2, commentDtos.get(1));

            bookingMapper.verify(() -> BookingDtoMapper.toBookingDto(lastBooking));
            bookingMapper.verify(() -> BookingDtoMapper.toBookingDto(nextBooking));
            commentMapper.verify(() -> CommentDtoMapper.toCommentDto(comment1));
            commentMapper.verify(() -> CommentDtoMapper.toCommentDto(comment2));
        }
    }

    @Test
    void updateItemFields() {
        Item item = new Item();
        item.setId(55);
        item.setName("OldName");
        item.setDescription("OldDesc");
        item.setAvailable(false);

        ItemUpdateDto updateDto = mock(ItemUpdateDto.class);
        when(updateDto.hasName()).thenReturn(true);
        when(updateDto.getName()).thenReturn("NewName");
        when(updateDto.hasDescription()).thenReturn(false);
        when(updateDto.hasAvailable()).thenReturn(true);
        when(updateDto.getAvailable()).thenReturn(true);

        Item updated = ItemDtoMapper.updateItemFields(item, updateDto);

        assertSame(item, updated);
        assertEquals("NewName", updated.getName());
        assertEquals("OldDesc", updated.getDescription(), "description не должна измениться, т.к. hasDescription() == false");
        assertTrue(updated.getAvailable());

        Item item2 = new Item();
        item2.setName("A");
        item2.setDescription("B");
        item2.setAvailable(false);

        ItemUpdateDto noChanges = mock(ItemUpdateDto.class);
        when(noChanges.hasName()).thenReturn(false);
        when(noChanges.hasDescription()).thenReturn(false);
        when(noChanges.hasAvailable()).thenReturn(false);

        Item result2 = ItemDtoMapper.updateItemFields(item2, noChanges);
        assertSame(item2, result2);
        assertEquals("A", result2.getName());
        assertEquals("B", result2.getDescription());
        assertFalse(result2.getAvailable());
    }
}