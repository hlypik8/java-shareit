package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.request.Request;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestDtoMapper {

    public static Request toRequest(RequestCreateDto requestCreateDto, Integer userId) {
        Request request = new Request();
        request.setRequester(userId);
        request.setDescription(requestCreateDto.getDescription());
        return request;
    }

    public static RequestDto toRequestDto(Request request) {
        List<ItemDto> items = request.getItems() == null ? Collections.emptyList() : request.getItems().stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toList());

        return new RequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequester(),
                request.getCreated(),
                items
        );
    }

    public static Request toRequest(RequestDto requestDto) {
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequester(requestDto.getRequester());
        request.setCreated(requestDto.getCreated());
        return request;
    }
}
