package org.skypro.bank.star.recommendations_service.rule;

import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    /**
     * Проверяем, подходит ли пользователь под правила рекомендации
     *
     * @param userId ID пользователя
     * @return RecommendationDTO если пользователь подходит, иначе Optional.empty()
     */
    Optional<RecommendationDTO> checkUser(UUID userId);
}