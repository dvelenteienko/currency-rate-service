package com.dvelenteienko.services.currency;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/currencies")
public class SimpleController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCurrency() {
        return "{message: 'OK'}";
    }
}
