package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final String sharerIdHeader = "X-Sharer-User-Id";

    private final RequestService requestService;

    @PostMapping
    public RequestDto addRequest(@RequestHeader(sharerIdHeader) Integer userId,
                                 @RequestBody RequestCreateDto requestCreateDto) throws NotFoundException {
        return requestService.createRequest(userId, requestCreateDto);
    }

    @GetMapping
    public List<RequestDto> getUserRequestsList(@RequestHeader(sharerIdHeader) Integer userId) throws NotFoundException {
        return requestService.getUserRequestsList(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@PathVariable Integer requestId) throws NotFoundException {
        return requestService.getRequestDtoById(requestId);
    }
}
