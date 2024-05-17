package com.dvelenteienko.services.currency.domain.entity.payload;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ErrorResponse {

    private HttpStatus statusCode;
    private String message;
    private String requestedUrl;

}
