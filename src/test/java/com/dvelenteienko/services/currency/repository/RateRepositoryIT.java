package com.dvelenteienko.services.currency.repository;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DBRider
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase
class RateRepositoryIT {

    @Autowired
    private CurrencyRateRepository testee;

    @Test
    @DataSet(cleanBefore = true, value = "services/currency/repository/CurrencyRateRepository-input.yml")
    void currencyRateRepository_WhenFindAllByBaseAndDateBetweenOrderByDateDesc_ThenReturnRates() {
        LocalDateTime dateFrom = LocalDateTime.of(2021, 12, 11, 23, 59, 59);
        LocalDateTime dateTo = LocalDateTime.of(2021, 12, 11, 23, 59, 59);
        Currency defaultCurrency = Currency.builder().setCode("USD").build();
//        List<Rate> rates = testee.findAllByBaseCurrencyCodeAndDateBetweenOrderByDateDesc(defaultCurrency, dateFrom, dateTo);

//        assertThat(rates)
//                .hasSize(2)
//                .allMatch(p -> p.getDate().equals(dateFrom) && p.getBase().getCode().equals("USD"));
    }

    @Test
    @ExpectedDataSet(ignoreCols = {"id"}, value = "services/currency/repository/CurrencyRateRepository-expected.yml")
    @DataSet(cleanBefore = true, value = "services/currency/repository/CurrencyRateRepository-save-input.yml")
    void currencyRateRepository_WhenSaveAndFlush_ThenExpected() {
        LocalDateTime rateDateDame = LocalDateTime.of(2021, 12, 13, 23, 59, 59);

        Currency defaultCurrency = Currency.builder()
                .setId(UUID.fromString("2f99e193-7056-4d19-97f9-ba8335ae8be9"))
                .setCode("EUR")
//                .setType(CurrencyType.BASE)
//                .setRates(List.of())
                .build();
        Rate expected = testee.saveAndFlush(Rate.builder()
                .setId(UUID.fromString("9ba71bd5-2f31-1313-a7f1-2c72fc7b0527"))
//                .setSource("CAD")
                .setBase(defaultCurrency)
                .setDate(rateDateDame)
                .setRate(3.2332)
                .build());

        assertThat(expected.getRate()).isEqualTo(3.2332);
//        assertThat(expected.getDate()).isEqualTo(rateDateDame);
//        assertThat(expected.getBase()).isEqualTo("EUR");
//        assertThat(expected.getSource()).isEqualTo("CAD");
    }


    @Configuration
    @EnableAutoConfiguration
    @EntityScan("com.dvelenteienko.services.currency.domain.entity")
    @EnableJpaRepositories("com.dvelenteienko.services.currency.repository")
    static class TestConfig {

    }
}
