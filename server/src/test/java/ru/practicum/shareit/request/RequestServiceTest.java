package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RequestService requestService;


    @Test
    void createRequest_shouldSaveAndReturnDto_whenUserExists() throws NotFoundException {
        Integer userId = 1;
        RequestCreateDto createDto = new RequestCreateDto();
        createDto.setDescription("Нужен молоток");

        Request savedRequest = new Request();
        savedRequest.setId(10);
        savedRequest.setRequester(userId);
        savedRequest.setDescription("Нужен молоток");
        savedRequest.setCreated(LocalDateTime.of(2023, 1, 1, 10, 0));

        when(userService.isUserExists(userId)).thenReturn(true);
        when(requestRepository.save(any(Request.class))).thenReturn(savedRequest);

        RequestDto result = requestService.createRequest(userId, createDto);

        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getDescription()).isEqualTo("Нужен молоток");
        assertThat(result.getRequester()).isEqualTo(1);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void createRequest_shouldThrowException_whenUserNotFound() {
        Integer userId = 2;
        RequestCreateDto createDto = new RequestCreateDto();
        when(userService.isUserExists(userId)).thenReturn(false);

        assertThatThrownBy(() -> requestService.createRequest(userId, createDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID: 2 не найден");
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getUserRequestsList_shouldReturnList_whenUserExists() throws NotFoundException {
        Integer userId = 3;
        Request request = new Request();
        request.setId(5);
        request.setRequester(userId);
        request.setDescription("Нужна дрель");
        request.setCreated(LocalDateTime.of(2023, 2, 1, 12, 0));

        when(userService.isUserExists(userId)).thenReturn(true);
        when(requestRepository.findAllByRequesterOrderByCreatedDesc(userId))
                .thenReturn(List.of(request));

        List<RequestDto> result = requestService.getUserRequestsList(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(5);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void getUserRequestsList_shouldThrowException_whenUserNotFound() {
        Integer userId = 99;
        when(userService.isUserExists(userId)).thenReturn(false);

        assertThatThrownBy(() -> requestService.getUserRequestsList(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID: 99 не найден");
    }

    @Test
    void getAllRequests_shouldReturnMappedDtos() {
        Request request = new Request();
        request.setId(7);
        request.setRequester(1);
        request.setDescription("Need bike");
        request.setCreated(LocalDateTime.of(2023, 3, 10, 15, 0));

        when(requestRepository.findAll()).thenReturn(List.of(request));

        List<RequestDto> result = requestService.getAllRequests();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(7);
        assertThat(result.get(0).getDescription()).isEqualTo("Need bike");
    }

    @Test
    void getRequestDtoById_shouldReturnDto_whenExists() throws NotFoundException {
        Integer requestId = 8;
        Request request = new Request();
        request.setId(requestId);
        request.setRequester(1);
        request.setDescription("Need laptop");
        request.setCreated(LocalDateTime.of(2023, 4, 5, 9, 0));

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        RequestDto result = requestService.getRequestDtoById(requestId);

        assertThat(result.getId()).isEqualTo(8);
        assertThat(result.getDescription()).isEqualTo("Need laptop");
    }

    @Test
    void getRequestDtoById_shouldThrowException_whenNotFound() {
        when(requestRepository.findById(123)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.getRequestDtoById(123))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("запрос вещи с ID: 123 не найден");
    }

    @Test
    void getRequestById_shouldReturnEntity_whenExists() throws NotFoundException {
        Integer requestId = 9;
        Request request = new Request();
        request.setId(requestId);
        request.setDescription("Need table");

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        Request result = requestService.getRequestById(requestId);

        assertThat(result.getId()).isEqualTo(9);
        assertThat(result.getDescription()).isEqualTo("Need table");
    }

    @Test
    void getRequestById_shouldThrowException_whenNotFound() {
        when(requestRepository.findById(500)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.getRequestById(500))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("запрос вещи с ID: 500 не найден");
    }
}