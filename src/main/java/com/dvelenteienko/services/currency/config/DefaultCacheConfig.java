package com.dvelenteienko.services.currency.config;

import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@AllArgsConstructor
public class DefaultCacheConfig {

    public static final String CURRENCY_CACHE_NAME = "currencies";
    public static final String RATE_CACHE_NAME = "rates";

    private final CacheConfigProperties cacheConfigProperties;

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CURRENCY_CACHE_NAME, RATE_CACHE_NAME);
    }

}
