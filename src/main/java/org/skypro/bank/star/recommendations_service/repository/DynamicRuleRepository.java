package org.skypro.bank.star.recommendations_service.repository;

import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с динамическими правилами рекомендаций.
 * Обеспечивает доступ к данным правил в PostgreSQL базе данных.
 */
@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {

    /**
     * Находит все динамические правила, отсортированные по идентификатору.
     * @return список динамических правил, отсортированных по ID
     */
    @Query("SELECT dr FROM DynamicRule dr ORDER BY dr.id")
    List<DynamicRule> findAllByOrderById();

}

