package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.exceptions.BookingNotvalidException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto mockItemDto(Integer id, String name, String description, Boolean available) {
        ItemDto dto = Mockito.mock(ItemDto.class);
        when(dto.getId()).thenReturn(id);
        when(dto.getName()).thenReturn(name);
        when(dto.getDescription()).thenReturn(description);
        when(dto.getAvailable()).thenReturn(available);
        return dto;
    }

    private ItemWithBookingAndCommentsDto mockItemWithBookingDto(Integer id, String name) {
        ItemWithBookingAndCommentsDto dto = Mockito.mock(ItemWithBookingAndCommentsDto.class);
        when(dto.getId()).thenReturn(id);
        when(dto.getName()).thenReturn(name);
        return dto;
    }

    private CommentDto mockCommentDto(Integer id, String text, String authorName) {
        CommentDto dto = Mockito.mock(CommentDto.class);
        when(dto.getId()).thenReturn(id);
        when(dto.getText()).thenReturn(text);
        when(dto.getAuthorName()).thenReturn(authorName);
        return dto;
    }

    @Test
    @DisplayName("POST /items - success")
    void addItem_success() throws Exception {
        String requestJson = """
                {"name":"Лопата","description":"для снега","available":true,"requestId":null}
                """;

        ItemDto returned = mockItemDto(1, "Лопата", "для снега", true);

        when(itemService.addItem(any(ItemCreateDto.class), eq(10))).thenReturn(returned);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 10)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Лопата"))
                .andExpect(jsonPath("$.description").value("для снега"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).addItem(any(ItemCreateDto.class), eq(10));
    }

    @Test
    @DisplayName("POST /items - NotFoundException -> 404")
    void addItem_notFound_returns404() throws Exception {
        String requestJson = """
                {"name":"Вилка","description":"металл","available":true}
                """;

        when(itemService.addItem(any(ItemCreateDto.class), eq(99)))
                .thenThrow(new NotFoundException("Пользователь с ID: 99 не найден"));

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 99)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка параметра"))
                .andExpect(jsonPath("$.description", Matchers.containsString("99")));

        verify(itemService, times(1)).addItem(any(ItemCreateDto.class), eq(99));
    }

    @Test
    @DisplayName("PATCH /items/{id} - success")
    void updateItem_success() throws Exception {
        int userId = 7;
        int itemId = 5;
        String requestJson = """
                {"name":"Дрель","description":"новая","available":false}
                """;

        ItemDto returned = mockItemDto(itemId, "Дрель", "новая", false);

        when(itemService.updateItem(any(ItemUpdateDto.class), eq(userId), eq(itemId))).thenReturn(returned);

        mvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, userId)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemService, times(1)).updateItem(any(ItemUpdateDto.class), eq(userId), eq(itemId));
    }

    @Test
    @DisplayName("GET /items/{id} - success")
    void getItemById_success() throws Exception {
        int itemId = 12;
        ItemWithBookingAndCommentsDto returned = mockItemWithBookingDto(itemId, "Велосипед");

        when(itemService.getItemDtoById(itemId)).thenReturn(returned);

        mvc.perform(get("/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Велосипед"));

        verify(itemService, times(1)).getItemDtoById(itemId);
    }


    @Test
    @DisplayName("GET /items (user's items) - success")
    void getItemsByUserId_success() throws Exception {
        int userId = 33;
        ItemWithBookingAndCommentsDto dto1 = mockItemWithBookingDto(1, "A");
        ItemWithBookingAndCommentsDto dto2 = mockItemWithBookingDto(2, "B");

        when(itemService.getItemsByUserId(userId)).thenReturn(List.of(dto1, dto2));

        mvc.perform(get("/items")
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(itemService, times(1)).getItemsByUserId(userId);
    }

    @Test
    @DisplayName("GET /items/search?text= - returns results")
    void searchItems_success() throws Exception {
        ItemDto dto1 = mockItemDto(10, "Молоток", "для гвоздей", true);
        ItemDto dto2 = mockItemDto(11, "Кувалда", "тяжёлая", true);

        when(itemService.searchItems("молот")).thenReturn(List.of(dto1, dto2));

        mvc.perform(get("/items/search")
                        .param("text", "молот"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[1].id").value(11));

        verify(itemService, times(1)).searchItems("молот");
    }

    @Test
    @DisplayName("POST /items/{itemId}/comment - success")
    void addComment_success() throws Exception {
        int itemId = 77;
        int userId = 4;
        String requestJson = """
                {"text":"Отличная вещь"}
                """;

        CommentDto returned = mockCommentDto(99, "Отличная вещь", "Анна");

        when(itemService.createComment(any(CommentCreateDto.class), eq(itemId), eq(userId))).thenReturn(returned);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.text").value("Отличная вещь"))
                .andExpect(jsonPath("$.authorName").value("Анна"));

        verify(itemService, times(1)).createComment(any(CommentCreateDto.class), eq(itemId), eq(userId));
    }

    @Test
    @DisplayName("POST /items/{itemId}/comment - BookingNotvalidException -> 400")
    void addComment_bookingInvalid_returns400() throws Exception {
        int itemId = 88;
        int userId = 5;
        String requestJson = """
                {"text":"Не могу добавить комментарий"}
                """;

        when(itemService.createComment(any(CommentCreateDto.class), eq(itemId), eq(userId)))
                .thenThrow(new BookingNotvalidException("Аренда не найдена"));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка параметра"))
                .andExpect(jsonPath("$.description", Matchers.containsString("Аренда не найдена")));

        verify(itemService, times(1)).createComment(any(CommentCreateDto.class), eq(itemId), eq(userId));
    }
}