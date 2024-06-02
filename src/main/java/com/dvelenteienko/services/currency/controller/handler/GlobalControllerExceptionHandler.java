package com.dvelenteienko.services.currency.controller.handler;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.entity.payload.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<Object> handleCurrencyNotFound(NoSuchElementException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .requestedUrl(request.getRequestURI())
                .build();
        return buildResponseEntity(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleCurrencyConflicts(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .requestedUrl(request.getRequestURI())
                .build();
        return buildResponseEntity(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> validationList = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> String.format("%s %s", err.getField(), err.getDefaultMessage()))
                .toList();
        String messages = String.join(", ", validationList);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.resolve(status.value()))
                .message(messages)
                .requestedUrl(Api.BASE_URL + request.getContextPath())
                .build();
        return buildResponseEntity(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> validationList = ex.getAllValidationResults().stream()
                .map(err -> String.format("%s %s",
                        err.getMethodParameter().getParameterName(),
                        err.getResolvableErrors().get(0).getDefaultMessage()))
                .toList();
        String messages = String.join(", ", validationList);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.resolve(status.value()))
                .message(messages)
                .requestedUrl(Api.BASE_URL + request.getContextPath())
                .build();
        return buildResponseEntity(errorResponse);
    }

    private ResponseEntity<Object> buildResponseEntity(ErrorResponse errorResponse) {
        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
    }

}
