package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final String sharerIdHeader = "X-Sharer-User-Id";

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(sharerIdHeader) Integer userId,
                                             @RequestBody RequestCreateDto requestCreateDto) {
        return requestClient.createRequest(userId, requestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequestsList(@RequestHeader(sharerIdHeader) Integer userId){
        return requestClient.getUserRequestsList(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(){
        return requestClient.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Integer requestId){
        return requestClient.getRequestById(requestId);
    }
}
