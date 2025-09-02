package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public RequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createRequest(Integer userId, RequestCreateDto requestCreateDto) {
        return post("", userId, requestCreateDto);
    }

    public ResponseEntity<Object> getUserRequestsList(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests() {
        return get("/all", 0L);
    }

    public ResponseEntity<Object> getRequestById(Integer requestId) {
        Map<String, Object> params = Map.of(
                "requestId", requestId
        );
        return get("/" + requestId, 0L, params);
    }
}
