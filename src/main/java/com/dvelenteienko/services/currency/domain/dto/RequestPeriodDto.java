package com.dvelenteienko.services.currency.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder(setterPrefix = "set")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestPeriodDto {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime from;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime to;

    @AssertTrue
    @JsonIgnore
    public boolean isValid() {
        return !from.isAfter(to) && !to.isAfter(LocalDateTime.now());
    }
}
