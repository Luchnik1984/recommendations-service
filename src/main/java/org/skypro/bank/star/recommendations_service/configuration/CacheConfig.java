package org.skypro.bank.star.recommendations_service.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.skypro.bank.star.recommendations_service.cache.ProductTypeKey;
import org.skypro.bank.star.recommendations_service.cache.TransactionTypeKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class CacheConfig {

    @Value("${cache.size.max}")
    private int maxSize;

    @Bean
    public Cache<ProductTypeKey, Boolean> hasProductTypeCache() {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .recordStats()
                .build();
    }

    @Bean
    public Cache<TransactionTypeKey, BigDecimal> totalAmountCache() {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .recordStats()
                .build();
    }

    @Bean
    public Cache<ProductTypeKey, Boolean> depositsGreaterCache() {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .recordStats()
                .build();
    }

    @Bean
    public Cache<ProductTypeKey, Integer> getTransactionCountByProductTypeCache() {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .recordStats()
                .build();
    }
}
