package ru.bookservice.bookservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServerNotAvailableException extends BaseException {
    private final String error;

    public ServerNotAvailableException (String message, String error) {
        super(HttpStatus.valueOf(500), message);
        this.error = error;
    }
}
