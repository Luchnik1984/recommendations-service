package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationResponse;
import org.skypro.bank.star.recommendations_service.rule.RecommendationRuleSet;
import org.skypro.bank.star.recommendations_service.rule.executor.QueryExecutorFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    QueryExecutorFactory queryExecutorFactory;

    public RecommendationService(List<RecommendationRuleSet> ruleSets, QueryExecutorFactory queryExecutorFactory) {
        this.ruleSets = ruleSets;
        this.queryExecutorFactory = queryExecutorFactory;
    }
//
//    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
//        this.ruleSets = ruleSets;
//    }

    public RecommendationResponse getRecommendationsForUser(UUID userId) {


        List<RecommendationDTO> recommendations = queryExecutorFactory.createResponseWithRecommendations(userId);

        for (RecommendationRuleSet ruleSet : ruleSets) {
            Optional<RecommendationDTO> recommendationDTO = ruleSet.checkUser(userId);
            RecommendationDTO recommendationDTO1 = recommendationDTO.orElse(null);
            if (recommendationDTO1 != null)
                recommendations.add(recommendationDTO1);

        }

        return new RecommendationResponse(userId, recommendations);

    }
}
