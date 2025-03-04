package ru.bookservice.bookservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServerNotAvailableException extends BaseException {
    public ServerNotAvailableException (String message) {
        super(HttpStatus.valueOf(500), message);
    }
}
