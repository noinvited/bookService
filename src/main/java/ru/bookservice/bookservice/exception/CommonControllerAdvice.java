package ru.bookservice.bookservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import ru.bookservice.bookservice.dto.BadBookRequestResponse;

@ControllerAdvice(annotations = RestController.class)
public class CommonControllerAdvice {
    @ExceptionHandler(BadBookRequestException.class)
    public ResponseEntity<BadBookRequestResponse> handleInvalidRequestCategoryException(BadBookRequestException e) {
        return ResponseEntity.status(e.getStatus()).body(new BadBookRequestResponse(e.getMessage(), e.getId()));
    }
}