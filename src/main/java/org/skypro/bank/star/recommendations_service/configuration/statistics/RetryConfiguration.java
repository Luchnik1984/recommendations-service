package org.skypro.bank.star.recommendations_service.configuration.statistics;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Конфигурация Spring Retry.
 * Используется аннотация @Retryable в DynamicRuleEngine для обработки ошибок при обновлении БД.
 */
@Configuration
@EnableRetry
public class RetryConfiguration {
    // Spring Retry автоматически активируется через @EnableRetry
}
