package org.skypro.bank.star.recommendations_service.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.skypro.bank.star.recommendations_service.cache.ProductTypeKey;
import org.skypro.bank.star.recommendations_service.cache.TransactionTypeKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Конфигурация кэширования для запросов к основной БД.
 * Использует Caffeine для кэширования результатов часто выполняемых запросов.
 */

@Configuration
public class CacheConfig {

    @Value("${cache.size.max:${cache.size.max}}")
    private int maxSize;

    @Value("${cache.ttl.hours:24}")
    private int ttlHours;

    /**
     * Кэш для проверки наличия продуктов определенного типа у пользователя.
     * Ключ: ProductTypeKey (userId + productType)
     * Значение: Boolean (наличие продукта)
     * TTL обеспечивает актуальность данных при изменениях в основной БД
     */
    @Bean
    public Cache<ProductTypeKey, Boolean> hasProductTypeCache() {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttlHours, TimeUnit.HOURS)
                .recordStats()
                .build();
    }

    /**
     * Кэш для хранения сумм транзакций по типам продуктов и операций.
     * Ключ: TransactionTypeKey (userId + productType + transactionType)
     * Значение: BigDecimal (сумма транзакций)
     */
    @Bean
    public Cache<TransactionTypeKey, BigDecimal> totalAmountCache() {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttlHours, TimeUnit.HOURS)
                .recordStats()
                .build();
    }


    /**
     * Кэш для хранения количества транзакций по типам продуктов.
     * Ключ: ProductTypeKey (userId + productType)
     * Значение: Integer (количество транзакций)
     * TTL подходит для данных об активности пользователей
     */
    @Bean
    public Cache<ProductTypeKey, Integer> getTransactionCountByProductTypeCache() {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttlHours, TimeUnit.HOURS)
                .recordStats()
                .build();
    }
}
