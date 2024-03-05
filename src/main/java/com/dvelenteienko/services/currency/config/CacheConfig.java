package com.dvelenteienko.services.currency.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
@AllArgsConstructor
public class CacheConfig {

    public static final String CURRENCY_CACHE_NAME = "currencies";
    public static final String CURRENCY_CACHE_ENTRY_NAME = "currencyList";
    public static final String RATE_CACHE_NAME = "rates";
    public static final String RATE_CACHE_ENTITY_NAME = "rateList";

    private final CaffeineCacheConfigProperties caffeineCacheConfigProperties;

    @Bean
    @ConditionalOnProperty(name = "service.cache.default.enabled")
    public CacheManager defaultCacheManager() {
        return new ConcurrentMapCacheManager(CURRENCY_CACHE_NAME, RATE_CACHE_NAME);
    }

    @Bean
    @ConditionalOnProperty(name = "service.cache.default.enabled", matchIfMissing = true)
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("currencyRatesCM");
        cacheManager.setCacheNames(List.of(CURRENCY_CACHE_NAME, RATE_CACHE_NAME));
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    @Bean
    public CustomCacheResolver currencyCacheResolver(CacheManager cacheManager) {
        return new CustomCacheResolver(cacheManager, CURRENCY_CACHE_NAME, CURRENCY_CACHE_ENTRY_NAME);
    }

    @Bean
    public CustomCacheResolver rateCacheResolver(CacheManager cacheManager) {
        return new CustomCacheResolver(cacheManager, RATE_CACHE_NAME, RATE_CACHE_ENTITY_NAME);
    }

    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(caffeineCacheConfigProperties.getInitialCapacity())
                .maximumSize(caffeineCacheConfigProperties.getMaximunSize())
                .expireAfterWrite(Duration.ofMinutes(caffeineCacheConfigProperties.getExpirationMinutes()));
    }

}
