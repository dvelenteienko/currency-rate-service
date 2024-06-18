package com.dvelenteienko.services.currency.exception;

public class DefaultJwtTokenException extends RuntimeException {
    DefaultJwtTokenException(String message) {
        super(message);
    }
}
