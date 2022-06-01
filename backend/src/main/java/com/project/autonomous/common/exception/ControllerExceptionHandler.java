package com.project.autonomous.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

//    @ExceptionHandler(value = { ConstraintViolationException.class, DataIntegrityViolationException.class})
//    protected ResponseEntity<ErrorResponse> handleDataException() {
//        log.error("handleDataException throw Exception : {}", DUPLICATE_RESOURCE);
//        return ErrorResponse.toResponseEntity(DUPLICATE_RESOURCE);
//    }

    @ExceptionHandler(value = { CustomException.class })
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    // https://cheese10yun.github.io/spring-jpa-best-02/
    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final BindingResult bindingResult = e.getBindingResult();
        final List<FieldError> errors = bindingResult.getFieldErrors();

        return ErrorResponse.toResponseEntity(errors.get(0));
    }

}
