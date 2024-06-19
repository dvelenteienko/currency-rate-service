package com.dvelenteienko.services.currency.rest.jwt;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class JwtResponse implements Serializable {

    private String token;

}
