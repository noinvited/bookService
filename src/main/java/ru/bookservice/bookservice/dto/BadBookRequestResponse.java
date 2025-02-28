package ru.bookservice.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BadBookRequestResponse {
    private String message;
    private Long id;
}
