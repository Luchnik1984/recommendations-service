package org.skypro.bank.star.recommendations_service.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.skypro.bank.star.recommendations_service.cache.ProductTypeKey;
import org.skypro.bank.star.recommendations_service.cache.TransactionTypeKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<ProductTypeKey, Boolean> hasProductTypeCache() {
        return Caffeine.newBuilder()
                .recordStats()
                .build();
    }

    @Bean
    public Cache<TransactionTypeKey, BigDecimal> totalAmountCache() {
        return Caffeine.newBuilder()
                .recordStats()
                .build();
    }

    @Bean
    public Cache<ProductTypeKey, Boolean> depositsGreaterCache() {
        return Caffeine.newBuilder()
                .recordStats()
                .build();
    }

    @Bean
    public Cache<ProductTypeKey, Integer> getTransactionCountByProductTypeCache() {
        return Caffeine.newBuilder()
                .recordStats()
                .build();
    }
}
