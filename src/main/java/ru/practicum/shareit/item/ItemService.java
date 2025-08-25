package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto addItem(ItemCreateDto itemCreateDto, Integer ownerId) throws NotFoundException {
        log.info("Добавление вещи пользователем с ID {}", ownerId);

        if (!userService.isUserExists(ownerId)) {
            log.warn("Пользователь с ID: {} не найден", ownerId);
            throw new NotFoundException("Пользователь с ID: " + ownerId + " не найден");
        }

        Item createdItem = itemRepository.save(ItemDtoMapper.toItem(itemCreateDto, ownerId));

        log.debug("Вещь: {} успешно добавлена пользователю с ID: {}", createdItem, ownerId);

        return ItemDtoMapper.toDto(createdItem);
    }

    public ItemDto updateItem(ItemUpdateDto itemUpdateDto,
                              Integer userId,
                              Integer itemId) throws InvalidItemOwnerException, NotFoundException {
        log.info("Обновление вещи с ID: {} пользователем с ID {}", itemId, userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + itemId + " не найдена"));

        if (!userService.isUserExists(userId)) {
            log.warn("Пользователь с ID: {} не найден", userId);
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        if (!itemRepository.findById(itemId).get().getOwner().equals(userId)) {
            log.warn("Владелец вещи c ID: {} и пользователь не совпадают", userId);
            throw new InvalidItemOwnerException("Владелец вещи и пользователь не совпадают");
        }

        Item updatedItem = ItemDtoMapper.updateItemFields(item, itemUpdateDto);

        log.debug("Вещь успешно обновлена: {}", updatedItem.toString());

        return ItemDtoMapper.toDto(updatedItem);
    }

    public List<ItemWithBookingAndCommentsDto> getItemsByUserId(Integer userId) throws NotFoundException {
        log.info("Запрос всех вещей пользователя с ID: {}", userId);

        if (!userService.isUserExists(userId)) {
            log.warn("Пользователь с ID: {} не найден", userId);
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        List<Item> items = itemRepository.findItemsByOwner(userId);

        log.debug("Найдено {} вещей для пользователя {}", items.size(), userId);

        return items.stream().map(item -> {
            List<Booking> bookings = bookingRepository.findAllByItemId(item.getId());
            LocalDateTime now = LocalDateTime.now();

            Booking lastBooking = bookings.stream()
                    .filter(booking -> booking.getEnd().isAfter(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            Booking nextBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now) && booking.getStatus() == BookingStatus.APPROVED)
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            List<Comment> comments = commentRepository.findAllByItemId(item.getId());

            return ItemDtoMapper.toItemWithBookingAndCommentsDto(item, lastBooking, nextBooking, comments);
        }).toList();
    }

    public ItemWithBookingAndCommentsDto getItemById(Integer itemId) throws NotFoundException {
        log.info("Запрос вещи по ID: {}", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с ID: " + itemId + " не найдена"));

        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookings.stream()
                .filter(booking -> booking.getEnd().isAfter(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        Booking nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now) && booking.getStatus() == BookingStatus.APPROVED)
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        log.debug("Получена вещь с Id: {}, комментариев: {}", itemId, comments.size());

        return ItemDtoMapper.toItemWithBookingAndCommentsDto(item, lastBooking, nextBooking, comments);
    }

    public List<ItemDto> searchItems(String text) {
        log.info("Поиск вещей по тексту: '{}'", text);

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<ItemDto> items = itemRepository.searchItems(text).stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toList());

        log.debug("Найдено {} вещей по запросу '{}'", items.size(), text);

        return items;
    }

    public CommentDto createComment(CommentCreateDto commentCreateDto, Integer itemId, Integer userId)
            throws NotFoundException, BookingNotvalidException {
        log.info("Добавление нового комментария от пользователя {} вещи {}", userId, itemId);

        LocalDateTime now = LocalDateTime.now();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + itemId + " не найдена"));

        User user = UserDtoMapper.toUser(userService.getUserById(userId));

        if (!bookingRepository.existsByItemIdAndBookerIdAndEndBefore(itemId, userId, now)) {
            throw new BookingNotvalidException("Аренда по запросу не найдена. Комментарий не сохранен");
        }

        Comment comment = commentRepository.save(CommentDtoMapper.toComment(commentCreateDto, item, user));

        log.debug("Комментарйи успешно сохранен text: {}", comment.getText());

        return CommentDtoMapper.toCommentDto(comment);
    }
}
