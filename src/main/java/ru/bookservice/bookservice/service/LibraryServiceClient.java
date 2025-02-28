package ru.bookservice.bookservice.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.bookservice.bookservice.dto.LibraryResponse;

import java.util.List;

@Service
@AllArgsConstructor
public class LibraryServiceClient {
    private final RestClient restClient;

    @Value("${api.host}")
    private String apiHost;

    @Value("${api.port}")
    private int apiPort;

    public LibraryServiceClient() {
        restClient = RestClient.create();
    }

    public LibraryResponse getLibraryById(Long id) {
        var result = restClient.get()
                .uri(apiHost + ":" + apiPort + "/libraries/{id}", id)
                .retrieve()
                .body(LibraryResponse.class);

        return result;
    }
}