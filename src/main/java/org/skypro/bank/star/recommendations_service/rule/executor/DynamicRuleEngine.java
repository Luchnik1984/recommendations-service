package org.skypro.bank.star.recommendations_service.rule.executor;

import jakarta.transaction.Transactional;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.model.dynamic.RuleQuery;
import org.skypro.bank.star.recommendations_service.repository.statistics.RuleStatisticsRepository;
import org.skypro.bank.star.recommendations_service.repository.dynamic.DynamicRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DynamicRuleEngine {
    
    private static final Logger log = LoggerFactory.getLogger(DynamicRuleEngine.class);

    private final QueryExecutorFactory queryExecutorFactory;
    private final DynamicRuleRepository dynamicRuleRepository;
    private final RuleStatisticsRepository ruleStatisticsRepository;

    public DynamicRuleEngine(QueryExecutorFactory queryExecutorFactory, DynamicRuleRepository dynamicRuleRepository,
                             RuleStatisticsRepository ruleStatisticsRepository) {
        this.queryExecutorFactory = queryExecutorFactory;
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.ruleStatisticsRepository = ruleStatisticsRepository;
    }

    /**
     * Создает коллекцию рекомендаций DTO по идентификатору пользователя.
     * При срабатывании динамического правила инкрементируется счётчик статистики.
     * @param userId идентификатор пользователя
     * @return Список рекомендаций для пользователя
     */
    @Transactional
    public List<RecommendationDTO> createRecommendationDTOList(UUID userId) {

        List<DynamicRule> dynamicRules = dynamicRuleRepository.findAllByOrderById();
        List<RecommendationDTO> recommendationDTOList = new ArrayList<>();

        for (DynamicRule rule : dynamicRules) {
            boolean allRulesPassed = true;

            for (RuleQuery ruleQuery : rule.getRule()) {
                RuleQueryExecutor executor = queryExecutorFactory.getRuleQueryExecutor(ruleQuery);

                boolean response = executor.execute(userId, ruleQuery.getArguments());

                if (ruleQuery.isNegate() != response) {
                    allRulesPassed = false;
                    break;
                }
            }

            if (allRulesPassed) {
                // правило сработало инкрементируем статистику с retry-логикой
                incrementStatisticsWithRetry(rule.getId());

                recommendationDTOList.add(new RecommendationDTO(
                        rule.getProductId(),
                        rule.getProductName(),
                        rule.getProductText()
                ));
            }
        }
        return recommendationDTOList;
    }

    /**
     * Инкремент счётчика статистики с retry-логикой.
     * maxAttempts = 3, задержка = 100 мс, мультипликатор = 2.0.
     */
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2.0)
    )
    @Transactional
    public void incrementStatisticsWithRetry(UUID ruleId) {
        try {
            int updated = ruleStatisticsRepository.incrementCounter(ruleId);
            if (updated == 0) {
                log.warn("Statistics not found for rule {}, increment skipped", ruleId);
            } else {
                log.debug("Statistics incremented for rule {}", ruleId);
            }
        } catch (Exception e) {
            log.error("Error incrementing statistics for rule {} (will be retried)", ruleId, e);
            throw e; // пробрасываем, чтобы @Retryable сработал
        }
    }

}
