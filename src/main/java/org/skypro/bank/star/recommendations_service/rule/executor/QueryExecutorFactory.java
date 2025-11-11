package org.skypro.bank.star.recommendations_service.rule.executor;

import jakarta.annotation.PostConstruct;
import org.skypro.bank.star.recommendations_service.enums.QueryType;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationResponse;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.model.dynamic.RuleQuery;
import org.skypro.bank.star.recommendations_service.repository.dynamic.DynamicRuleRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class QueryExecutorFactory {

    private final Map<QueryType, RuleQueryExecutor> ruleQueryExecutorMap = new HashMap<>();
    private final List<RuleQueryExecutor> ruleQueryExecutors;
    private final DynamicRuleRepository dynamicRuleRepository;

    public QueryExecutorFactory(List<RuleQueryExecutor> ruleQueryExecutors, DynamicRuleRepository dynamicRuleRepository) {
        this.ruleQueryExecutors = ruleQueryExecutors;
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    /**
     * Метод создает HashMap с исполнителями правил, где ключ является QueryType,
     * а значение - сам класс исполнитель
     */
    @PostConstruct
    public void createMap() {
        for (RuleQueryExecutor ruleQueryExecutor : ruleQueryExecutors) {
            ruleQueryExecutorMap.put(ruleQueryExecutor.getSupportedQueryType(), ruleQueryExecutor);
        }
    }

    /**
     * Определяет обработчика для правила
     * @param ruleQuery правило
     * @return исполнитель
     */
    public RuleQueryExecutor getRuleQueryExecutor(RuleQuery ruleQuery){
        return ruleQueryExecutorMap.get(ruleQuery.getQuery());
    }



    public List<RecommendationDTO> createResponseWithRecommendations(UUID userId) {

        List<DynamicRule> dynamicRules = dynamicRuleRepository.findAllByOrderById();
        System.out.println(dynamicRules);
        List<RecommendationDTO> recommendationDTOList = new ArrayList<>();
        for (DynamicRule rule : dynamicRules) {
            boolean allRulesPassed = true;
            for (RuleQuery ruleQuery : rule.getRule()) {

                Boolean response = ruleQueryExecutorMap.get(ruleQuery.getQuery())
                        .execute(userId, ruleQuery.getArguments());

                if(ruleQuery.isNegate()) {
                    response = !response;
                }

                if (!response) {
                    allRulesPassed = false;
                    break;
                }
            }
            if (allRulesPassed) {
                recommendationDTOList.add(new RecommendationDTO(
                        rule.getId(),
                        rule.getProductName(),
                        rule.getProductText()));
            }
        }
        return recommendationDTOList;
    }


}
