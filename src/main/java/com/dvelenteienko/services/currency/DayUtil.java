package com.dvelenteienko.services.currency;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public final class DayUtil {

    public static LocalDate getValidFinancialDay(LocalDate date) {
        LocalDate validDate = date;
        int dayOfWeekNum = DayOfWeek.from(date).getValue();
        if(dayOfWeekNum > 5) {
            validDate = date.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
        }
        return validDate;
    }

}
