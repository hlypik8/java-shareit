package ru.practicum.shareit.item;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.error.exceptions.BookingNotvalidException;
import ru.practicum.shareit.error.exceptions.InvalidItemOwnerException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestService requestService;

    @InjectMocks
    private ItemService itemService;


    private ItemCreateDto mockCreateDto(Integer requestId, String name, String desc, Boolean available) {
        ItemCreateDto dto = mock(ItemCreateDto.class);
        when(dto.getRequestId()).thenReturn(requestId);
        when(dto.getName()).thenReturn(name);
        when(dto.getDescription()).thenReturn(desc);
        when(dto.getAvailable()).thenReturn(available);
        return dto;
    }

    private ItemUpdateDto mockUpdateDto(String name, String desc, Boolean available) {
        ItemUpdateDto dto = mock(ItemUpdateDto.class);
        when(dto.getName()).thenReturn(name);
        when(dto.getDescription()).thenReturn(desc);
        when(dto.getAvailable()).thenReturn(available);
        return dto;
    }

    private Item sampleItem(int id, String name, Integer owner) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(null);
        return item;
    }

    private Booking sampleBooking(int id, int itemId) {
        Booking b = mock(Booking.class);
        when(b.getId()).thenReturn(id);
        return b;
    }

    private Comment sampleComment(int id, String text) {
        Comment c = mock(Comment.class);
        when(c.getId()).thenReturn(id);
        when(c.getText()).thenReturn(text);
        return c;
    }

    private User sampleUser(int id, String name, String email) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        return u;
    }

    private CommentRepository commentRepository() {
        return commentRepository;
    }

    @Test
    @DisplayName("addItem: success without requestId")
    void addItem_success_withoutRequest() throws NotFoundException {
        int ownerId = 1;
        ItemCreateDto createDto = mockCreateDto(null, "Шляпа", "теплая", true);

        when(userService.isUserExists(ownerId)).thenReturn(true);

        Item toSave = new Item();
        toSave.setName("Шляпа");
        toSave.setDescription("теплая");
        toSave.setAvailable(true);
        toSave.setOwner(ownerId);

        Item saved = sampleItem(100, "Шляпа", ownerId);

        ItemDto expectedDto = mock(ItemDto.class);

        try (MockedStatic<ItemDtoMapper> mapper = Mockito.mockStatic(ItemDtoMapper.class)) {
            mapper.when(() -> ItemDtoMapper.toItem(createDto, ownerId, null)).thenReturn(toSave);
            when(itemRepository.save(toSave)).thenReturn(saved);
            mapper.when(() -> ItemDtoMapper.toDto(saved)).thenReturn(expectedDto);

            ItemDto result = itemService.addItem(createDto, ownerId);

            assertSame(expectedDto, result);
            verify(userService).isUserExists(ownerId);
            verify(itemRepository).save(toSave);
        }
    }

    @Test
    @DisplayName("addItem: success with requestId")
    void addItem_success_withRequest() throws NotFoundException {
        int ownerId = 2;
        int requestId = 55;
        ItemCreateDto createDto = mockCreateDto(requestId, "Лопата", "для снега", true);

        when(userService.isUserExists(ownerId)).thenReturn(true);

        Request request = mock(Request.class);
        when(requestService.getRequestById(requestId)).thenReturn(request);

        Item toSave = new Item();
        toSave.setName("Лопата");
        toSave.setOwner(ownerId);
        toSave.setRequest(request);

        Item saved = sampleItem(200, "Лопата", ownerId);

        ItemDto expectedDto = mock(ItemDto.class);

        try (MockedStatic<ItemDtoMapper> mapper = Mockito.mockStatic(ItemDtoMapper.class)) {
            mapper.when(() -> ItemDtoMapper.toItem(createDto, ownerId, request)).thenReturn(toSave);
            when(itemRepository.save(toSave)).thenReturn(saved);
            mapper.when(() -> ItemDtoMapper.toDto(saved)).thenReturn(expectedDto);

            ItemDto result = itemService.addItem(createDto, ownerId);

            assertSame(expectedDto, result);
            verify(requestService).getRequestById(requestId);
            verify(itemRepository).save(toSave);
        }
    }

    @Test
    @DisplayName("addItem: user not exists -> NotFoundException")
    void addItem_userNotExists_throws() {
        int ownerId = 3;
        ItemCreateDto createDto = mockCreateDto(null, "Вилка", "металл", true);

        when(userService.isUserExists(ownerId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.addItem(createDto, ownerId));

        verify(userService).isUserExists(ownerId);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    @DisplayName("updateItem: success if owner matches")
    void updateItem_success() throws NotFoundException, InvalidItemOwnerException {
        int userId = 10;
        int itemId = 20;

        Item existing = sampleItem(itemId, "Книга", userId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existing));
        when(userService.isUserExists(userId)).thenReturn(true);

        ItemUpdateDto updateDto = mockUpdateDto("Книга2", "описание", false);
        Item updated = sampleItem(itemId, "Книга2", userId);
        ItemDto expectedDto = mock(ItemDto.class);

        try (MockedStatic<ItemDtoMapper> mapper = Mockito.mockStatic(ItemDtoMapper.class)) {
            mapper.when(() -> ItemDtoMapper.updateItemFields(existing, updateDto)).thenReturn(updated);
            mapper.when(() -> ItemDtoMapper.toDto(updated)).thenReturn(expectedDto);

            ItemDto result = itemService.updateItem(updateDto, userId, itemId);

            assertSame(expectedDto, result);
            verify(itemRepository).findById(itemId);
            verify(userService).isUserExists(userId);
            mapper.verify(() -> ItemDtoMapper.updateItemFields(existing, updateDto));
        }
    }

    @Test
    @DisplayName("updateItem: invalid owner -> InvalidItemOwnerException")
    void updateItem_invalidOwner_throws() {
        int userId = 11;
        int itemId = 21;
        Item existing = sampleItem(itemId, "Телефон", 999); // owner != userId

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existing));
        when(userService.isUserExists(userId)).thenReturn(true);

        ItemUpdateDto updateDto = mockUpdateDto("Телефон новый", null, null);

        assertThrows(InvalidItemOwnerException.class, () -> itemService.updateItem(updateDto, userId, itemId));

        verify(itemRepository).findById(itemId);
        verify(userService).isUserExists(userId);
    }

    @Test
    @DisplayName("getItemsByUserId: success")
    void getItemsByUserId_success() throws NotFoundException {
        int userId = 30;
        when(userService.isUserExists(userId)).thenReturn(true);

        Item item = sampleItem(300, "Велосипед", userId);
        when(itemRepository.findItemsByOwner(userId)).thenReturn(List.of(item));

        Booking last = sampleBooking(1, item.getId());
        Booking next = sampleBooking(2, item.getId());
        when(bookingRepository.findTopByItemIdAndEndAfterOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(last);
        when(bookingRepository.findTopByItemIdAndStartAfterAndStatusOrderByStartAsc(eq(item.getId()),
                any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(next);

        Comment comment = sampleComment(5, "Great");
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        ItemWithBookingAndCommentsDto expected = mock(ItemWithBookingAndCommentsDto.class);

        try (MockedStatic<ItemDtoMapper> mapper = Mockito.mockStatic(ItemDtoMapper.class)) {
            mapper.when(() -> ItemDtoMapper.toItemWithBookingAndCommentsDto(item, last, next, List.of(comment)))
                    .thenReturn(expected);

            List<ItemWithBookingAndCommentsDto> result = itemService.getItemsByUserId(userId);

            assertEquals(1, result.size());
            assertSame(expected, result.get(0));

            verify(itemRepository).findItemsByOwner(userId);
            verify(bookingRepository).findTopByItemIdAndEndAfterOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class));
            verify(bookingRepository).findTopByItemIdAndStartAfterAndStatusOrderByStartAsc(eq(item.getId()),
                    any(LocalDateTime.class), eq(BookingStatus.APPROVED));
            verify(commentRepository).findAllByItemId(item.getId());
        }
    }

    @Test
    @DisplayName("getItemDtoById: success")
    void getItemDtoById_success() throws NotFoundException {
        int itemId = 400;
        Item item = sampleItem(itemId, "Клавиатура", 40);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Booking last = sampleBooking(10, itemId);
        Booking next = sampleBooking(11, itemId);
        when(bookingRepository.findTopByItemIdAndEndAfterOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(last);
        when(bookingRepository.findTopByItemIdAndStartAfterAndStatusOrderByStartAsc(eq(item.getId()),
                any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(next);

        Comment comment = sampleComment(7, "ok");
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));

        ItemWithBookingAndCommentsDto expected = mock(ItemWithBookingAndCommentsDto.class);

        try (MockedStatic<ItemDtoMapper> mapper = Mockito.mockStatic(ItemDtoMapper.class)) {
            mapper.when(() -> ItemDtoMapper.toItemWithBookingAndCommentsDto(item, last, next, List.of(comment)))
                    .thenReturn(expected);

            ItemWithBookingAndCommentsDto result = itemService.getItemDtoById(itemId);

            assertSame(expected, result);
            verify(itemRepository).findById(itemId);
            verify(commentRepository, times(1)).findAllByItemId(itemId); // helper to avoid IDE warnings
        }
    }

    @Test
    @DisplayName("searchItems")
    void searchItems() {
        // blank and null
        List<ItemDto> res1 = itemService.searchItems(null);
        assertTrue(res1.isEmpty());

        List<ItemDto> res2 = itemService.searchItems("   ");
        assertTrue(res2.isEmpty());

        // non-blank
        Item item1 = sampleItem(500, "Молоток", 50);
        Item item2 = sampleItem(501, "Молоточек", 50);
        when(itemRepository.searchItems("молот")).thenReturn(List.of(item1, item2));

        ItemDto dto1 = mock(ItemDto.class);
        ItemDto dto2 = mock(ItemDto.class);

        try (MockedStatic<ItemDtoMapper> mapper = Mockito.mockStatic(ItemDtoMapper.class)) {
            mapper.when(() -> ItemDtoMapper.toDto(item1)).thenReturn(dto1);
            mapper.when(() -> ItemDtoMapper.toDto(item2)).thenReturn(dto2);

            List<ItemDto> res = itemService.searchItems("молот");
            assertEquals(2, res.size());
            assertSame(dto1, res.get(0));
            assertSame(dto2, res.get(1));
        }
    }

    @Test
    @DisplayName("createComment: success when booking exists")
    void createComment_success() throws NotFoundException, BookingNotvalidException {
        int itemId = 600;
        int userId = 60;
        CommentCreateDto createDto = mock(CommentCreateDto.class);

        Item item = sampleItem(itemId, "Лампа", 6000);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        User user = sampleUser(userId, "N", "n@example.com");
        when(userService.getUserById(userId)).thenReturn(user);

        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(eq(itemId), eq(userId), any(LocalDateTime.class)))
                .thenReturn(true);

        Comment prepared = sampleComment(70, "cool");
        try (MockedStatic<CommentDtoMapper> mapper = Mockito.mockStatic(CommentDtoMapper.class)) {
            mapper.when(() -> CommentDtoMapper.toComment(createDto, item, user)).thenReturn(prepared);
            when(commentRepository.save(prepared)).thenReturn(prepared);
            CommentDto expectedDto = mock(CommentDto.class);
            mapper.when(() -> CommentDtoMapper.toCommentDto(prepared)).thenReturn(expectedDto);

            CommentDto result = itemService.createComment(createDto, itemId, userId);

            assertSame(expectedDto, result);
            verify(commentRepository).save(prepared);
        }
    }

    @Test
    @DisplayName("createComment: no booking -> BookingNotvalidException")
    void createComment_noBooking() throws NotFoundException {
        int itemId = 601;
        int userId = 61;
        CommentCreateDto createDto = mock(CommentCreateDto.class);

        Item item = sampleItem(itemId, "Шампунь", 7000);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        User user = sampleUser(userId, "U", "u@example.com");
        when(userService.getUserById(userId)).thenReturn(user);

        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(eq(itemId), eq(userId), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(BookingNotvalidException.class, () -> itemService.createComment(createDto, itemId, userId));

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("getItemById: not found -> NotFoundException")
    void getItemById_notFound_throws() {
        int itemId = 9999;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId));

        verify(itemRepository).findById(itemId);
    }
}