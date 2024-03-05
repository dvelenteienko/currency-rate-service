package com.dvelenteienko.services.currency.config;

import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CustomCacheResolver {

    private final CacheManager cacheManager;
    private final String cacheName;
    private final String cacheNameEntry;

    public <T> List<T> getFromCache() {
        List<T> cached = new ArrayList<>();
        Cache cache = cacheManager.getCache(this.cacheName);
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(this.cacheNameEntry);
            if (valueWrapper != null) {
                cached = (List<T>) valueWrapper.get();
            }
        }
        return cached;
    }

    public <T> List<T> putToCache(List<T> dtos) {
        List<T> fromCache = getFromCache();
        if (!fromCache.isEmpty()) {
            List<T> collector = new ArrayList<>(fromCache);
            collector.addAll(dtos);
            cacheManager.getCache(cacheName).put(cacheNameEntry, collector);
            return collector;
        } else {
            cacheManager.getCache(cacheName).put(cacheNameEntry, dtos);
            return dtos;
        }
    }
}
