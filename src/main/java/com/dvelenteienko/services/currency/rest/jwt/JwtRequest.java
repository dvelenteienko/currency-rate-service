package com.dvelenteienko.services.currency.rest.jwt;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class JwtRequest implements Serializable {

    private String username;
    private String password;

}
