package com.dvelenteienko.services.currency.config;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(List.of(DefaultCacheConfig.CURRENCY_CACHE_NAME, DefaultCacheConfig.RATE_CACHE_NAME));
        cacheManager.setAllowNullValues(false);
    }

}
