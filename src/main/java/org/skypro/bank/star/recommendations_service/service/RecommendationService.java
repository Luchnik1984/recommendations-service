package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationResponse;
import org.skypro.bank.star.recommendations_service.rule.RecommendationRuleSet;
import org.skypro.bank.star.recommendations_service.rule.executor.DynamicRuleEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    private final DynamicRuleEngine dynamicRuleEngine;

    public RecommendationService(List<RecommendationRuleSet> ruleSets, DynamicRuleEngine dynamicRuleEngine) {
        this.ruleSets = ruleSets;
        this.dynamicRuleEngine = dynamicRuleEngine;
    }

    /**
     * Метод получения рекомендаций для пользователя по его идентификатору
     * используются динамические и статические правила
     *
     * @param userId идентификатор пользователя
     * @return список рекомендаций DTO
     */
    @Transactional(readOnly = true)
    public RecommendationResponse getRecommendationsForUser(UUID userId) {

        List<RecommendationDTO> recommendations = addToListRecommendationWithStaticRulesForUserID(
                ruleSets,
                userId,
                dynamicRuleEngine.createRecommendationDTOList(userId));

        return new RecommendationResponse(userId, recommendations);
    }

    /**
     * Метод получения рекомендаций для пользователя по его идентификатору
     * используются только динамические правила
     *
     * @param userId идентификатор пользователя
     * @return список рекомендаций DTO
     */
    public RecommendationResponse getRecommendationsForUserWithoutStaticRule(UUID userId) {
        List<RecommendationDTO> recommendations = dynamicRuleEngine.createRecommendationDTOList(userId);
        return new RecommendationResponse(userId, recommendations);
    }

    /**
     * Метод получения рекомендаций для пользователя по его идентификатору
     * используются только статические правила
     *
     * @param userId идентификатор пользователя
     * @return список рекомендаций DTO
     */
    public RecommendationResponse getRecommendationsForUserWithoutDynamicRules(UUID userId) {
        List<RecommendationDTO> recommendations =
                addToListRecommendationWithStaticRulesForUserID(ruleSets, userId, new ArrayList<>());
        return new RecommendationResponse(userId, recommendations);
    }

    /**
     * Метод добавления в список DTO рекомендаций для пользователя по его идентификатору
     * для статических правил
     *
     * @param ruleSets        список статических правил
     * @param userId          идентификатор пользователя
     * @param recommendations список DTO рекомендаций
     * @return обновленный список DTO рекомендаций
     */
    private List<RecommendationDTO> addToListRecommendationWithStaticRulesForUserID(
            List<RecommendationRuleSet> ruleSets,
            UUID userId,
            List<RecommendationDTO> recommendations) {

        for (RecommendationRuleSet ruleSet : ruleSets) {
            Optional<RecommendationDTO> recommendationDTO = ruleSet.checkUser(userId);
            recommendationDTO.ifPresent(recommendations::add);
        }
        return recommendations;
    }
}
