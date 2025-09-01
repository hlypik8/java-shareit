package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.Request;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequestDtoMapperTest {

    @Test
    void toRequest_shouldMapFieldsCorrectly() {
        RequestCreateDto dto = new RequestCreateDto();
        dto.setDescription("Нужна дрель");

        Request request = RequestDtoMapper.toRequest(dto, 42);

        assertThat(request.getRequester()).isEqualTo(42);
        assertThat(request.getDescription()).isEqualTo("Нужна дрель");
        assertThat(request.getId()).isNull();
        assertThat(request.getCreated()).isNotNull();
    }

    @Test
    void toRequestDto_shouldMapFieldsAndEmptyItems() {
        Request request = new Request();
        request.setId(1);
        request.setDescription("Нужна дрель");
        request.setRequester(99);
        request.setCreated(LocalDateTime.of(2023, 5, 15, 10, 0));
        request.setItems(null);

        RequestDto dto = RequestDtoMapper.toRequestDto(request);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getRequester()).isEqualTo(99);
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2023, 5, 15, 10, 0));
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void toRequestDto_shouldMapItemsWhenPresent() {
        Item item = new Item();
        item.setId(5);
        item.setName("Отвертка");
        item.setDescription("Крестовая");
        item.setAvailable(true);

        Request request = new Request();
        request.setId(2);
        request.setDescription("Нужна отвертка");
        request.setRequester(77);
        request.setCreated(LocalDateTime.of(2023, 6, 20, 15, 30));
        request.setItems(List.of(item));

        RequestDto dto = RequestDtoMapper.toRequestDto(request);

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getItems()).hasSize(1);
        ItemDto mappedItem = dto.getItems().get(0);
        assertThat(mappedItem.getId()).isEqualTo(5);
        assertThat(mappedItem.getName()).isEqualTo("Отвертка");
        assertThat(mappedItem.getDescription()).isEqualTo("Крестовая");
        assertThat(mappedItem.getAvailable()).isTrue();
    }

    @Test
    void toRequest_fromDto_shouldMapAllFields() {
        RequestDto dto = new RequestDto(
                10,
                "Нужна дрель",
                55,
                LocalDateTime.of(2023, 7, 1, 12, 0),
                List.of()
        );

        Request request = RequestDtoMapper.toRequest(dto);

        assertThat(request.getId()).isEqualTo(10);
        assertThat(request.getDescription()).isEqualTo("Нужна дрель");
        assertThat(request.getRequester()).isEqualTo(55);
        assertThat(request.getCreated()).isEqualTo(LocalDateTime.of(2023, 7, 1, 12, 0));
    }
}