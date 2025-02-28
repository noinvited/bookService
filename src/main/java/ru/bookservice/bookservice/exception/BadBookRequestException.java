package ru.bookservice.bookservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadBookRequestException extends BaseException{
    private final Long id;

    public BadBookRequestException(String message, Long id) {
        super(HttpStatus.valueOf(400), message);
        this.id = id;
    }
}
