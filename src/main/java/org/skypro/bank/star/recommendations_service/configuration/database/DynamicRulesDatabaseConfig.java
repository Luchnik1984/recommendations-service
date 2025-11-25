package org.skypro.bank.star.recommendations_service.configuration.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.sql.DataSource;

/**
 * Конфигурация для второй базы данных (PostgreSQL), используемой для хранения динамических правил.
 * Эта конфигурация активна только в НЕ-тестовых профилях (prod, dev, default, integration-test).
 * <p>
 * В тестовом профиле заменяется на TestDatabaseConfig с H2 in-memory базами.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "org.skypro.bank.star.recommendations_service.repository.dynamic",
                "org.skypro.bank.star.recommendations_service.repository.statistics"
        },
        entityManagerFactoryRef = "dynamicRulesEntityManagerFactory",
        transactionManagerRef = "dynamicRulesTransactionManager"
)
@Profile("!test") // АКТИВЕН ТОЛЬКО В НЕ-ТЕСТОВЫХ ПРОФИЛЯХ (включая integration-test)
public class DynamicRulesDatabaseConfig {
    /**
     * Создает и настраивает DataSource для PostgreSQL базы динамических правил.
     * DataSource используется для установления соединений с БД и управления connection pool.
     * Использует HikariCP как implementation connection pool для лучшей производительности.
     * Настройки connection pool берутся из application.properties.
     * Аннотация @Primary указывает, что этот DataSource должен использоваться по умолчанию
     * для всех операций JPA, если не указано иное.
     *
     * @param url      URL базы данных PostgreSQL (из .env или application.properties)
     * @param username имя пользователя PostgreSQL (из .env или application.properties)
     * @param password пароль пользователя PostgreSQL (из .env или application.properties)
     * @return настроенный DataSource для PostgreSQL с HikariCP connection pool
     */
    @Bean(name = "dynamicRulesDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource dynamicRulesDataSource(
            @Value("${POSTGRES_URL:jdbc:postgresql://localhost:5432/dynamic_rules}") String url,
            @Value("${POSTGRES_USERNAME:postgres}") String username,
            @Value("${POSTGRES_PASSWORD:password}") String password) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");

        // Настройки connection pool (HikariCP)
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setIdleTimeout(300000);      // 5 минут
        dataSource.setConnectionTimeout(20000); // 20 секунд
        dataSource.setMaxLifetime(1200000);     // 20 минут

        // Дополнительные настройки для лучшей производительности
        dataSource.setLeakDetectionThreshold(60000); // Обнаружение утечек соединений
        dataSource.setConnectionTestQuery("SELECT 1"); // Query для проверки соединения

        return dataSource;
    }

    /**
     * Создает и настраивает EntityManagerFactory для JPA.
     * EntityManagerFactory управляет жизненным циклом JPA EntityManager и
     * является точкой входа для всех JPA операций.
     * Настраивает Hibernate как провайдера JPA с PostgreSQL диалектом.
     * Указывает пакеты для сканирования JPA сущностей.
     *
     * @return настроенный EntityManagerFactory для JPA операций
     */
    @Bean(name = "dynamicRulesEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean dynamicRulesEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        // Устанавливаем DataSource для PostgreSQL
        em.setDataSource(dynamicRulesDataSource(
                "${POSTGRES_URL:jdbc:postgresql://localhost:5432/dynamic_rules}",
                "${POSTGRES_USERNAME:postgres}",
                "${POSTGRES_PASSWORD:password}"
        ));

        // Указываем пакеты, в которых находятся JPA сущности для динамических правил
        em.setPackagesToScan(
                "org.skypro.bank.star.recommendations_service.model.dynamic",
                "org.skypro.bank.star.recommendations_service.model.statistics"
        );

        // Настраиваем Hibernate как провайдера JPA
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false); // Отключаем автоматическое создание DDL
        vendorAdapter.setShowSql(false);

        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    /**
     * Создает и настраивает менеджер транзакций для JPA.
     * TransactionManager управляет границами транзакций для операций с БД,
     * обеспечивая согласованность данных.
     * Использует JpaTransactionManager, который интегрируется с JPA и Hibernate.
     *
     * @return настроенный PlatformTransactionManager для управления транзакциями
     */
    @Bean(name = "dynamicRulesTransactionManager")
    @Primary
    public PlatformTransactionManager dynamicRulesTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(dynamicRulesEntityManagerFactory().getObject());

        transactionManager.setDefaultTimeout(30); // Таймаут транзакций 30 секунд
        transactionManager.setRollbackOnCommitFailure(true); // Откат при ошибке коммита

        return transactionManager;
    }
}
