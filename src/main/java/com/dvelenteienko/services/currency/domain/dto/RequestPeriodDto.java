package com.dvelenteienko.services.currency.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder(setterPrefix = "set")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestPeriodDto {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;

    @AssertTrue
    @JsonIgnore
    protected boolean isValid() {
        return !from.isAfter(to) && !to.isAfter(LocalDate.now());
    }
}
