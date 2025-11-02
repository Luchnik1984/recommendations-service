package org.skypro.bank.star.recommendations_service;
import org.skypro.bank.star.recommendations_service.repository.TestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class US1FinalTest implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(US1FinalTest.class);
    private final TestRepository testRepository;

    public US1FinalTest(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== US1: FINAL CONFIGURATION CHECK ===");

        try {
            // Проверяем подключение к реальной базе данных
            int usersCount = testRepository.getUsersCount();
            int productsCount = testRepository.getProductsCount();
            int transactionsCount = testRepository.getTransactionsCount();

            logger.info(" Connection to the database was established successfully");
            logger.info(" USERS table: {} records", usersCount);
            logger.info("PRODUCTS table: {} records", productsCount);
            logger.info("TRANSACTIONS table: {} records", transactionsCount);

            logger.info(" US1 COMPLETED SUCCESSFULLY!");
            logger.info(" Spring Boot the project is configured with an H2 database (read-only)");
            logger.info(" DataSource and JdbcTemplate are configured correctly");
            logger.info(" We can move on to US2: Creating a package structure");

        } catch (Exception e) {
            logger.error(" ! Error during execution US1: {}", e.getMessage());
            logger.error("! Check that the transaction.mv.db file is located in src/main/resources/");
        }
    }
}