package org.skypro.bank.star.recommendations_service.rule.executor;

import jakarta.annotation.PostConstruct;
import org.skypro.bank.star.recommendations_service.enums.QueryType;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationResponse;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.model.dynamic.RuleQuery;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class QueryExecutorFactory {

    private final Map<QueryType, RuleQueryExecutor> ruleQueryExecutorMap = new HashMap<>();
    private final List<RuleQueryExecutor> ruleQueryExecutors;

    public QueryExecutorFactory(List<RuleQueryExecutor> ruleQueryExecutors) {
        this.ruleQueryExecutors = ruleQueryExecutors;
    }

    @PostConstruct
    public void createMap() {
        for (RuleQueryExecutor ruleQueryExecutor : ruleQueryExecutors) {
            ruleQueryExecutorMap.put(ruleQueryExecutor.getSupportedQueryType(), ruleQueryExecutor);
        }
    }

    public RecommendationResponse createResponseWithRecommendations(UUID userId, DynamicRule[] dynamicRules) {

        List<RecommendationDTO> recommendationDTOList = new ArrayList<>();
        for (DynamicRule rule : dynamicRules) {
            boolean allRulesPassed = true;
            for (RuleQuery ruleQuery : rule.getRule()) {
                if (!ruleQueryExecutorMap.get(QueryType.valueOf(ruleQuery.getArguments().get(0)))
                        .execute(userId, ruleQuery.getArguments())) {
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
        return new RecommendationResponse(userId,recommendationDTOList);
    }


}
