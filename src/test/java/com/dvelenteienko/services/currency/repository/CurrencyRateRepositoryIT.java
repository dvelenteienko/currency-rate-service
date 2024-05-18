package com.dvelenteienko.services.currency.repository;

import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DBRider
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase
class CurrencyRateRepositoryIT {

    @Autowired
    private CurrencyRateRepository testee;

    @Test
    @DataSet(cleanBefore = true, value = "services/currency/repository/CurrencyRateRepository-input.yml")
    void currencyRateRepository_WhenFindAllByBaseAndDateBetweenOrderByDateDesc_ThenReturnRates() {
        LocalDateTime dateFrom = LocalDateTime.of(2021, 12, 11, 23, 59, 59);
        LocalDateTime dateTo = LocalDateTime.of(2021, 12, 11, 23, 59, 59);

        List<CurrencyRate> currencyRates = testee.findAllByBaseCurrencyCodeAndDateBetweenOrderByDateDesc("USD", dateFrom, dateTo);

        assertThat(currencyRates)
                .hasSize(2)
                .allMatch(p -> p.getDate().equals(dateFrom) && p.getBase().equals("USD"));
    }

    @Test
    @ExpectedDataSet(ignoreCols = {"id"}, orderBy = {"base"}, value = "services/currency/repository/CurrencyRateRepository-expected.yml")
    @DataSet(cleanBefore = true, value = "services/currency/repository/CurrencyRateRepository-save-input.yml")
    void currencyRateRepository_WhenSaveAndFlush_ThenExpected() {
        LocalDateTime rateDateDame = LocalDateTime.of(2021, 12, 13, 23, 59, 59);

        CurrencyRate expected = testee.saveAndFlush(new CurrencyRate(null, "CAD", "EUR", rateDateDame, 3.2332));

        assertThat(expected.getRate()).isEqualTo(3.2332);
        assertThat(expected.getDate()).isEqualTo(rateDateDame);
        assertThat(expected.getBase()).isEqualTo("EUR");
        assertThat(expected.getSource()).isEqualTo("CAD");
    }


    @Configuration
    @EnableAutoConfiguration
    @EntityScan("com.dvelenteienko.services.currency.domain.entity")
    @EnableJpaRepositories("com.dvelenteienko.services.currency.repository")
    static class TestConfig {

    }
}
