package ru.bookservice.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerNotAvailableResponse {
    private String message;
    private String error;
}
