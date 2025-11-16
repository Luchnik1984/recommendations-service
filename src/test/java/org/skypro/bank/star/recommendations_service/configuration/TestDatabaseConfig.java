package org.skypro.bank.star.recommendations_service.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * ТЕСТОВАЯ КОНФИГУРАЦИЯ БАЗ ДАННЫХ
 * ТОЧНАЯ КОПИЯ MAIN КОНФИГУРАЦИИ С ПЕРЕМЕННЫМИ
 */
@TestConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.skypro.bank.star.recommendations_service.repository.dynamic",
        entityManagerFactoryRef = "dynamicRulesEntityManagerFactory",
        transactionManagerRef = "dynamicRulesTransactionManager"
)
@Profile("test")
public class TestDatabaseConfig {

    @Value("${application.recommendations-db.url}")
    private String h2DatabaseUrl;

    /**
     * DataSource для основной H2 базы (IN-MEMORY ВЕРСИЯ)
     */
    @Bean(name = "recommendationsDataSource")
    @Qualifier("recommendationsDataSource")
    public DataSource recommendationsDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(h2DatabaseUrl);
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setReadOnly(true);
        dataSource.setMaximumPoolSize(5);
        return dataSource;
    }

    /**
     * JdbcTemplate для основной H2 базы
     */
    @Bean(name = "recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate(@Qualifier("recommendationsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * DataSource для PostgreSQL базы динамических правил
     * ИСПРАВЛЕННАЯ ВЕРСИЯ - используем DataSourceBuilder для правильной работы с @ConfigurationProperties
     */
    @Bean(name = "dynamicRulesDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dynamicRulesDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * EntityManagerFactory для PostgreSQL
     * ИСПРАВЛЕННАЯ ВЕРСИЯ - используем внедрение зависимостей
     */
    @Bean(name = "dynamicRulesEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean dynamicRulesEntityManagerFactory(
            @Qualifier("dynamicRulesDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.skypro.bank.star.recommendations_service.model.dynamic");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(false);
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    /**
     * TransactionManager для PostgreSQL
     * ИСПРАВЛЕННАЯ ВЕРСИЯ - используем внедрение зависимостей
     */
    @Bean(name = "dynamicRulesTransactionManager")
    @Primary
    public PlatformTransactionManager dynamicRulesTransactionManager(
            @Qualifier("dynamicRulesEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        transactionManager.setDefaultTimeout(30);
        transactionManager.setRollbackOnCommitFailure(true);
        return transactionManager;
    }
}