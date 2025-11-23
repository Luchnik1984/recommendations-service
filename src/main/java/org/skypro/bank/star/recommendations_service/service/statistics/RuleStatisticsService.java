package org.skypro.bank.star.recommendations_service.service.statistics;


import org.skypro.bank.star.recommendations_service.model.dto.statistics.RuleStat;
import org.skypro.bank.star.recommendations_service.model.dto.statistics.RuleStatsResponse;
import org.skypro.bank.star.recommendations_service.model.statistics.RuleStatistics;
import org.skypro.bank.star.recommendations_service.repository.statistics.RuleStatisticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RuleStatisticsService {

    private final RuleStatisticsRepository statisticsRepository;

    public RuleStatisticsService(RuleStatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    /** Возвращает весь список статистики для API */
    @Transactional(readOnly = true)
    public RuleStatsResponse getAllStatistics() {
        List<RuleStat> stats = statisticsRepository.findAllStats().stream()
                .map(stat -> new RuleStat(
                        stat.getRuleId().toString(),
                        stat.getTriggerCount()))
                .collect(Collectors.toList());
        return new RuleStatsResponse(stats);
    }

    /** Возвращает статистику по конкретному правилу (или null, если не найдено) */
    @Transactional(readOnly = true)
    public RuleStat getStatisticsByRuleId(String ruleId) {
        return statisticsRepository.findById(UUID.fromString(ruleId))
                .map(stat -> new RuleStat(
                        stat.getRuleId().toString(),
                        stat.getTriggerCount()))
                .orElse(null);
    }

    /** Суммарное число срабатываний всех правил */
    @Transactional(readOnly = true)
    public Long getTotalTriggers() {
        return statisticsRepository.findAllStats().stream()
                .mapToLong(RuleStatistics::getTriggerCount)
                .sum();
    }

    /** Число правил, которые ни разу не срабатывали */
    @Transactional(readOnly = true)
    public Long getUnusedRulesCount() {
        return statisticsRepository.findAllStats().stream()
                .filter(stat -> stat.getTriggerCount() == 0)
                .count();
    }
}