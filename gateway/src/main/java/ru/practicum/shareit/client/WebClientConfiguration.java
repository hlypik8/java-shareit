package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.user.UserClient;

@Configuration
public class WebClientConfiguration {

    @Value("${shareit-server.url}")
    private String serverUrl;

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory());
    }

    @Bean
    @Qualifier("bookingRestTemplate")
    public RestTemplate bookingRestTemplate(RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                .build();
    }

    @Bean
    @Qualifier("itemRestTemplate")
    public RestTemplate itemRestTemplate(RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items"))
                .build();
    }

    @Bean
    @Qualifier("userRestTemplate")
    public RestTemplate userRestTemplate(RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                .build();
    }

    @Bean
    @Qualifier("requestRestTemplate")
    public RestTemplate requestRestTemplate(RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/requests"))
                .build();
    }

    @Bean
    public BookingClient bookingClient(@Qualifier("bookingRestTemplate") RestTemplate restTemplate) {
        return new BookingClient(restTemplate);
    }

    @Bean
    public ItemClient itemClient(@Qualifier("itemRestTemplate") RestTemplate restTemplate) {
        return new ItemClient(restTemplate);
    }

    @Bean
    public UserClient userClient(@Qualifier("userRestTemplate") RestTemplate restTemplate) {
        return new UserClient(restTemplate);
    }

    @Bean
    public RequestClient requestClient(@Qualifier("requestRestTemplate") RestTemplate restTemplate) {
        return new RequestClient(restTemplate);
    }
}
