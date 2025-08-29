package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(ItemCreateDto itemCreateDto, Integer userId) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> updateItem(ItemUpdateDto itemUpdateDto, Integer userId, Integer itemId) {
        return patch("/" + itemId, userId, itemUpdateDto);
    }

    public ResponseEntity<Object> getItemDtoById(Integer itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getItemsByUserId(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> params = Map.of(
            "text", text
        );
        return get("/search?text=" + text, 0L, params);
    }

    public ResponseEntity<Object> createComment(CommentCreateDto commentCreateDto, Integer itemId, Integer userId) {
        return post("/" + itemId + "/comment", userId, commentCreateDto);
    }
}
