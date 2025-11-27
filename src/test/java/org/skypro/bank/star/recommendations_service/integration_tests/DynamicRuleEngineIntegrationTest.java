package org.skypro.bank.star.recommendations_service.integration_tests;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.skypro.bank.star.recommendations_service.model.statistics.RuleStatistics;
import org.skypro.bank.star.recommendations_service.repository.statistics.RuleStatisticsRepository;
import org.skypro.bank.star.recommendations_service.rule.executor.DynamicRuleEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DynamicRuleEngineIntegrationTest {

    @Autowired
    private DynamicRuleEngine dynamicRuleEngine;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RuleStatisticsRepository statisticsRepository;

    @Test
    void incrementStatisticsWithRetry_incrementsCounter() {
        UUID ruleId = UUID.randomUUID();
        RuleStatistics stats = new RuleStatistics(ruleId);
        statisticsRepository.saveAndFlush(stats);

        dynamicRuleEngine.incrementStatisticsWithRetry(ruleId);
        entityManager.clear();

        RuleStatistics updated = statisticsRepository.findById(ruleId).orElseThrow();
        assertThat(updated.getTriggerCount()).isEqualTo(1L);
    }
}
