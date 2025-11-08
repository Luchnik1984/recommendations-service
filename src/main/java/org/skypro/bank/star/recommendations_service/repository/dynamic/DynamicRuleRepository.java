package org.skypro.bank.star.recommendations_service.repository.dynamic;

import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository для работы с динамическими правилами рекомендаций в PostgreSQL БД.
 * Предоставляет базовые CRUD операции и специализированные запросы для работы
 * с динамическими правилами. Наследует все стандартные методы из JpaRepository.
 * Все методы выполняются в контексте транзакций, управляемых DynamicRulesTransactionManager.
 */
@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
    /**
     * Находит все динамические правила, отсортированные по идентификатору.
     * @return список всех динамических правил в БД, отсортированный по ID
     */
    List<DynamicRule> findAllByOrderById();

    /**
     * Проверяет существование правила по идентификатору продукта.
     * Может использоваться для валидации при создании новых правил.
     * @param productId UUID продукта для проверки
     * @return true если правило с указанным productId существует, false в противном случае
     */
    boolean existsByProductId(UUID productId);

    /**
     * Находит правило по идентификатору рекомендуемого продукта.
     * @param productId UUID продукта для поиска
     * @return Optional с найденным правилом или empty если правило не найдено
     */
    Optional<DynamicRule> findByProductId(UUID productId);

    /**
     * Находит все правила, содержащие указанное название продукта (case-insensitive поиск).
     * @param productName название продукта для поиска (частичное совпадение)
     * @return список правил, содержащих указанное название продукта
     */
    List<DynamicRule> findByProductNameContainingIgnoreCase(String productName);

    /**
     * Пользовательский JPQL запрос для подсчета общего количества правил в БД.
     * @return общее количество динамических правил
     */
    @Query("SELECT COUNT(dr) FROM DynamicRule dr")
    long countAllRules();

    /**
     * Пользовательский JPQL запрос для поиска правил по типу запроса.
     * Использует JOIN для поиска в связанной таблице rule_queries.
     * @param queryType тип запроса для поиска
     * @return список правил, содержащих указанный тип запроса
     */
    @Query("SELECT DISTINCT dr FROM DynamicRule dr JOIN dr.rule rq WHERE rq.query = :queryType")
    List<DynamicRule> findByQueryType(@Param("queryType") String queryType);
}
