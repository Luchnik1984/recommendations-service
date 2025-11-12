package org.skypro.bank.star.recommendations_service.rule.executor;

import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.model.dynamic.RuleQuery;
import org.skypro.bank.star.recommendations_service.repository.dynamic.DynamicRuleRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DynamicRuleEngine {

    private final QueryExecutorFactory queryExecutorFactory;
    private final DynamicRuleRepository dynamicRuleRepository;

    public DynamicRuleEngine(QueryExecutorFactory queryExecutorFactory, DynamicRuleRepository dynamicRuleRepository) {
        this.queryExecutorFactory = queryExecutorFactory;
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    /**
     * Создает коллекцию рекомендаций DTO по идентификатору пользователя
     * @param userId идентификатор пользователя
     * @return Список рекомендаций для пользователя
     */
    public List<RecommendationDTO> createRecommendationDTOList(UUID userId) {

        List<DynamicRule> dynamicRules = dynamicRuleRepository.findAllByOrderById();
        System.out.println(dynamicRules);
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
                recommendationDTOList.add(new RecommendationDTO(
                        rule.getProductId(),
                        rule.getProductName(),
                        rule.getProductText()));
            }
        }
        return recommendationDTOList;
    }

}
