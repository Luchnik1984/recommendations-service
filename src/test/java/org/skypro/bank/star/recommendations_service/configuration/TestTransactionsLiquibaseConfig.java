package org.skypro.bank.star.recommendations_service.configuration;

import liquibase.integration.spring.SpringLiquibase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * КОНФИГУРАЦИЯ LIQUIBASE ДЛЯ ТРАНЗАКЦИОННОЙ БАЗЫ В ТЕСТАХ.
 * Применяет миграции только к H2 in-memory базе транзакций
 * Не затрагивает JPA базу динамических правил
 */
@Configuration
@Profile("test")
public class TestTransactionsLiquibaseConfig {

    /**
     * Primary Liquibase бин для транзакционной базы
     * Spring Boot будет использовать этот бин вместо автоконфигурации
     */
    @Bean
    @Primary
    public SpringLiquibase transactionsLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();

        // Используем DataSource который указывает на транзакционную базу
        liquibase.setDataSource(dataSource);

        // Changelog для тестовой транзакционной базы
        liquibase.setChangeLog("classpath:db/changelog/v1/changelog-master.yaml");

        liquibase.setShouldRun(true);
        return liquibase;
    }
}

