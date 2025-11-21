package org.skypro.bank.star.recommendations_service.repository.statistics;

import org.skypro.bank.star.recommendations_service.model.statistics.RuleStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RuleStatisticsRepository extends JpaRepository<RuleStatistics, UUID> {

    /**
     * Атомарное увеличение счетчика срабатываний правила
     * @param ruleId идентификатор правила
     * @return количество обновленных записей
     */
    @Modifying
    @Query("UPDATE RuleStatistics rs SET rs.triggerCount = rs.triggerCount + 1 WHERE rs.ruleId = :ruleId")
    int incrementCounter(@Param("ruleId") UUID ruleId);

    /**
     * Получение всей статистики
     * @return список всех записей статистики
     */
    @Query("SELECT rs FROM RuleStatistics rs ORDER BY rs.triggerCount DESC")
    List<RuleStatistics> findAllStats();

    /**
     * Удаление статистики по ID правила
     * @param ruleId идентификатор правила
     */
    void deleteByRuleId(UUID ruleId);
}
