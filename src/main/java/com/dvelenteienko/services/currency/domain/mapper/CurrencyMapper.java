package com.dvelenteienko.services.currency.domain.mapper;

import com.dvelenteienko.services.currency.domain.dto.CurrencyDTO;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDTO;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    @Mapping(source = "base.code", target = "baseCurrency")
    @Mapping(source = "source.code", target = "sourceCurrency")
    CurrencyRateDTO rateToCurrencyRateDTO(Rate currencyRates);

    CurrencyDTO currencyToCurrencyDTO(Currency currency);

    List<CurrencyRateDTO> ratesToCurrencyRatesDTOs(List<Rate> currencyRates);

    @Mapping(source = "baseCurrency", target = "base.code")
    @Mapping(source = "sourceCurrency", target = "source.code")
    Rate currencyRateDtoToRate(CurrencyRateDTO currencyRatesDTO);

    List<Rate> currencyRatesDtosToRates(List<CurrencyRateDTO> currencyRatesDTOs);
    List<CurrencyDTO> currenciesToCurrencyDtos(List<Currency> currencies);
}
