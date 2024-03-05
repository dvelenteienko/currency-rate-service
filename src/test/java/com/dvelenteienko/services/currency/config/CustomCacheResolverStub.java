package com.dvelenteienko.services.currency.config;

import org.springframework.cache.CacheManager;

import java.util.Collections;
import java.util.List;

public class CustomCacheResolverStub extends CustomCacheResolver {

    public CustomCacheResolverStub(CacheManager cacheManager, String cacheName, String cacheNameEntry) {
        super(cacheManager, cacheName, cacheNameEntry);
    }

    @Override
    public <T> List<T> getFromCache() {
        return Collections.emptyList();
    }

    @Override
    public <T> List<T> putToCache(List<T> dtos) {
        return Collections.emptyList();
    }
}
