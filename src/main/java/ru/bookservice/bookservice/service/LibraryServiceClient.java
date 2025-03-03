package ru.bookservice.bookservice.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.bookservice.bookservice.dto.LibraryResponse;
import ru.bookservice.bookservice.exception.ServerNotAvailableException;

@Service
@AllArgsConstructor
public class LibraryServiceClient {
    private final RestClient restClient;

    @Value("${api.host}")
    private String apiHost;

    @Value("${api.port}")
    private int apiPort;

    /**
     * Конструктор по умолчанию, инициализирующий RestClient.
     */
    public LibraryServiceClient() {
        restClient = RestClient.create();
    }

    /**
     * Возвращает информацию о библиотеке по её идентификатору, используя внешний API.
     *
     * @param id идентификатор библиотеки
     * @return объект {@link LibraryResponse}, содержащий информацию о библиотеке
     * @throws ServerNotAvailableException если сервер временно недоступен или произошла ошибка при подключении
     */
    public LibraryResponse getLibraryById(Long id) throws ServerNotAvailableException {
        try{
            return restClient.get()
                    .uri(apiHost + ":" + apiPort + "/libraries/{id}", id)
                    .retrieve()
                    .body(LibraryResponse.class);
        } catch (Exception e){
            throw new ServerNotAvailableException("Сервер временно недоступен", "Ошибка при подключении к серверу, попробуйте зайти позже");
        }
    }
}