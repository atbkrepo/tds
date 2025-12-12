package com.demo.parking.controller;

import com.demo.parking.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Clock;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(getErrorDto(e));
    }

    private static ErrorResponse getErrorDto(Exception e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .timestamp(LocalDateTime.now(Clock.systemUTC()).toString())
                .build();
    }
}
