package org.skypro.bank.star.recommendations_service.configuration.statistics;

import jakarta.annotation.PostConstruct;
import org.skypro.bank.star.recommendations_service.model.statistics.RuleStatistics;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.repository.statistics.RuleStatisticsRepository;
import org.skypro.bank.star.recommendations_service.repository.dynamic.DynamicRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Инициализатор статистики правил.
 * Выполняется при старте приложения.
 */
@Component
public class RuleStatisticsInitializer {

    private static final Logger log = LoggerFactory.getLogger(RuleStatisticsInitializer.class);

    private final RuleStatisticsRepository ruleStatisticsRepository;
    private final DynamicRuleRepository dynamicRuleRepository;

    public RuleStatisticsInitializer(RuleStatisticsRepository ruleStatisticsRepository,
                                     DynamicRuleRepository dynamicRuleRepository) {
        this.ruleStatisticsRepository = ruleStatisticsRepository;
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    /**
     * Инициализация статистики при старте приложения.
     * Проверяет, что для всех существующих правил есть записи в statistics.
     * Если какого-то правила нет в статистике, создаёт запись с count = 0.
     */
    @PostConstruct
    @Transactional
    public void initializeStatistics() {
        try {
            log.info("Starting RuleStatistics initialization...");



            List<DynamicRule> allRules = dynamicRuleRepository.findAll();
            log.info("Found {} dynamic rules in database", allRules.size());

            int createdCount = 0;
            for (DynamicRule rule : allRules) {
                if (!ruleStatisticsRepository.existsById(rule.getId())) {
                    RuleStatistics statistics = new RuleStatistics(rule.getId());
                    ruleStatisticsRepository.save(statistics);
                    createdCount++;
                    log.debug("Created statistics for rule {}", rule.getId());
                }
            }

            log.info("RuleStatistics initialization completed. Created {} new statistics records", createdCount);

            log.info("RuleStatistics initialization completed (DynamicRuleRepository not available)");
        } catch (Exception e) {
            log.error("Error during RuleStatistics initialization", e);
        }
    }

    /**
     * Обеспечить наличие статистики для конкретного правила.
     *
     * @param ruleId идентификатор правила
     */
    @Transactional
    public void ensureStatisticsForRule(UUID ruleId) {
        if (!ruleStatisticsRepository.existsById(ruleId)) {
            RuleStatistics statistics = new RuleStatistics(ruleId);
            ruleStatisticsRepository.save(statistics);
            log.info("Created statistics for rule {}", ruleId);
        }
    }
}