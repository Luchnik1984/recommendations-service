package org.skypro.bank.star.recommendations_service.unit_tests;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skypro.bank.star.recommendations_service.model.statistics.RuleStatistics;
import org.skypro.bank.star.recommendations_service.repository.statistics.RuleStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RuleStatisticsRepositoryTest {

    @Autowired
    private RuleStatisticsRepository statisticsRepository;

    @Autowired
    private EntityManager entityManager;

    private UUID testRuleId;

    @BeforeEach
    void setUp() {
        testRuleId = UUID.randomUUID();
        statisticsRepository.deleteAll();
    }

    @Test
    void shouldCreateStatisticsWithZeroCount() {

        RuleStatistics stats = new RuleStatistics(testRuleId);

        RuleStatistics saved = statisticsRepository.save(stats);

        assertThat(saved.getRuleId()).isEqualTo(testRuleId);
        assertThat(saved.getTriggerCount()).isEqualTo(0L);
    }

    @Test
    @Transactional
    void shouldIncrementCounterAtomically() {

        RuleStatistics stats = new RuleStatistics(testRuleId);
        statisticsRepository.save(stats);

        int updated = statisticsRepository.incrementCounter(testRuleId);
        entityManager.clear();

        assertThat(updated).isEqualTo(1);
        Optional<RuleStatistics> result = statisticsRepository.findById(testRuleId);
        assertThat(result).isPresent();
        assertThat(result.get().getTriggerCount()).isEqualTo(1L);
    }

    @Test
    @Transactional
    void shouldIncrementMultipleTimes() {

        RuleStatistics stats = new RuleStatistics(testRuleId);
        statisticsRepository.save(stats);

        for (int i = 0; i < 5; i++) {
            statisticsRepository.incrementCounter(testRuleId);
        }
        entityManager.clear();

        Optional<RuleStatistics> result = statisticsRepository.findById(testRuleId);
        assertThat(result).isPresent();
        assertThat(result.get().getTriggerCount()).isEqualTo(5L);
    }

    @Test
    void shouldFindAllStats() {

        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();

        RuleStatistics stats1 = new RuleStatistics(ruleId1);
        stats1.setTriggerCount(10L);

        RuleStatistics stats2 = new RuleStatistics(ruleId2);
        stats2.setTriggerCount(5L);

        statisticsRepository.saveAll(List.of(stats1, stats2));

        List<RuleStatistics> allStats = statisticsRepository.findAllStats();

        assertThat(allStats).hasSize(2);
        assertThat(allStats.get(0).getTriggerCount()).isGreaterThanOrEqualTo(allStats.get(1).getTriggerCount());
    }

    @Test
    void shouldDeleteByRuleId() {

        RuleStatistics stats = new RuleStatistics(testRuleId);
        statisticsRepository.save(stats);

        statisticsRepository.deleteByRuleId(testRuleId);

        Optional<RuleStatistics> result = statisticsRepository.findById(testRuleId);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleNonExistentRuleIdForIncrement() {

        UUID nonExistentId = UUID.randomUUID();

        int updated = statisticsRepository.incrementCounter(nonExistentId);

        assertThat(updated).isEqualTo(0);
    }
}