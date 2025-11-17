package org.skypro.bank.star.recommendations_service.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ПРОСТАЯ КОНФИГУРАЦИЯ ДЛЯ JPA В ТЕСТАХ
 */
@TestConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.skypro.bank.star.recommendations_service.repository.dynamic"
)
@Profile("test")
public class TestJpaConfig {
    // Spring Boot автоматически настроит JPA для H2 в тестовом профиле
}