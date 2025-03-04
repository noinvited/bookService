package ru.bookservice.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class BookResponse {
    private String title;
    private String author;
    private List<LibraryResponse> libraries;
}
