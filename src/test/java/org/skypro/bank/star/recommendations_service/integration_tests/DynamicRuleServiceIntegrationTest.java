package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.Test;
import org.skypro.bank.star.recommendations_service.enums.QueryType;
import org.skypro.bank.star.recommendations_service.model.dto.RuleQueryDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleRequestDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleResponse;
import org.skypro.bank.star.recommendations_service.model.statistics.RuleStatistics;
import org.skypro.bank.star.recommendations_service.repository.dynamic.DynamicRuleRepository;
import org.skypro.bank.star.recommendations_service.repository.statistics.RuleStatisticsRepository;
import org.skypro.bank.star.recommendations_service.service.DynamicRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DynamicRuleServiceIntegrationTest {

    @Autowired
    private DynamicRuleService dynamicRuleService;

    @Autowired
    private DynamicRuleRepository dynamicRuleRepository;

    @Autowired
    private RuleStatisticsRepository statisticsRepository;

    @Test
    void whenRuleCreated_thenStatisticsIsCreated() {
        UUID productId = UUID.randomUUID();

        RuleQueryDTO queryDTO = new RuleQueryDTO(
                QueryType.USER_OF,
                new String[]{"DEBIT"},
                false
        );


        RuleRequestDTO request = new RuleRequestDTO(
                "TEST_RULE",
                productId,
                "Тестовое правило",
                List.of(queryDTO)
        );


        RuleResponse response = dynamicRuleService.postDynamicRule(request);
        UUID ruleId = response.ruleID();

        assertThat(dynamicRuleRepository.existsById(ruleId)).isTrue();
        assertThat(statisticsRepository.existsById(ruleId)).isTrue();
        assertThat(statisticsRepository.findById(ruleId).get().getTriggerCount()).isZero();
    }
}