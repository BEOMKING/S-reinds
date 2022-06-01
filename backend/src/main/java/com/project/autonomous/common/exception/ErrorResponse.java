package com.project.autonomous.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ErrorResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().name())
                .code(errorCode.name())
                .message(errorCode.getDetail())
                .build()
            );
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(FieldError error) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .code(error.getCode())
                .message(error.getDefaultMessage() + "에러 필드 : " + error.getField() + "에러 값 : " + error.getRejectedValue())
                .build()
            );
    }

}
