package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoMapper;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;


    public RequestDto createRequest(Integer userId, RequestCreateDto requestCreateDto) throws NotFoundException {
        log.info("Добавление нового запроса вещи");

        if (!userService.isUserExists(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        Request request = requestRepository.save(RequestDtoMapper.toRequest(requestCreateDto, userId));

        log.info("Запрос вещи успешно добавлен id: {}", request.getId());

        return RequestDtoMapper.toRequestDto(request);
    }

    public List<RequestDto> getUserRequestsList(Integer userId) throws NotFoundException {
        log.info("Запрос запросов вещей пользователя с ID: {}", userId);

        if (!userService.isUserExists(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        List<RequestDto> result = requestRepository.findAllByRequesterOrderByCreatedDesc(userId).stream()
                .map(RequestDtoMapper::toRequestDto)
                .collect(Collectors.toList());

        log.info("Запрос вещей пользователя успешно выполнен size: {}", result.size());

        return result;
    }

    public List<RequestDto> getAllRequests() {
        log.info("Запрос всех запросов");

        List<RequestDto> result = requestRepository.findAll().stream()
                .map(RequestDtoMapper::toRequestDto)
                .collect(Collectors.toList());

        log.info("Запрос всех запросов успешно выполнен size: {}", result.size());

        return result;
    }

    public RequestDto getRequestDtoById(Integer requestId) throws NotFoundException {
        log.info("Запрос запроса по ID: {}", requestId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("запрос вещи с ID: " + requestId + " не найден"));
        log.info("Запрос успешно выполнен ID: {}", requestId);
        return RequestDtoMapper.toRequestDto(request);
    }

    public Request getRequestById(Integer requestId) throws NotFoundException {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("запрос вещи с ID: " + requestId + " не найден"));
    }

}
