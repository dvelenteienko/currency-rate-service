package com.dvelenteienko.services.currency.config;

import com.dvelenteienko.services.currency.util.RequestPeriod;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableCaching
@AllArgsConstructor
public class CacheConfig {

    public static final String CURRENCY_CACHE_NAME = "currencies";
    public static final String RATE_CACHE_NAME = "rates";

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CURRENCY_CACHE_NAME, RATE_CACHE_NAME);
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    @SuppressWarnings("unchecked")
    @Bean
    public KeyGenerator ratesKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder key = new StringBuilder();
            key.append(method.getName()).append("_");
            for (Object param : params) {
                if (param.getClass().isAssignableFrom(String.class)) {
                    key.append(param).append("_");
                }
                if (param.getClass().isAssignableFrom(List.class)) {
                    List<String> codes = (List<String>) param;
                    key.append(Strings.join(codes, '-'));
                }
                if (param.getClass().isAssignableFrom(RequestPeriod.class)) {
                    RequestPeriod requestPeriod = (RequestPeriod) param;
                    key.append(String.format("_%s-%s", requestPeriod.getFrom().toString(),
                            requestPeriod.getTo().toString()));
                }
            }
            return key.toString();
        };
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(200)
                .expireAfterAccess(Duration.ofMinutes(10));
    }
}
