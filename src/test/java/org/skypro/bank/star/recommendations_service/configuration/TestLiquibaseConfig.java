package org.skypro.bank.star.recommendations_service.configuration;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * КОНФИГУРАЦИЯ ДЛЯ ТЕСТОВЫХ ДАННЫХ LIQUIBASE.
 * Применяет тестовые данные только в тестовом профиле
 */
@TestConfiguration
@Profile("test")
public class TestLiquibaseConfig {

    /**
     * Второй Liquibase для тестовых данных.
     * Работает с той же БД, но другим changelog
     */
    @Bean
    @ConditionalOnProperty(name = "spring.liquibase.test-data.enabled", havingValue = "true")
    public SpringLiquibase testDataLiquibase(@Qualifier("dynamicRulesDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/test-data/changelog-master.yaml");
        liquibase.setShouldRun(true);

        // Отключаем создание таблицы Liquibase (она уже создана основным Liquibase)
        liquibase.setDatabaseChangeLogTable("DATABASECHANGELOG");
        liquibase.setDatabaseChangeLogLockTable("DATABASECHANGELOGLOCK");

        return liquibase;
    }
}
