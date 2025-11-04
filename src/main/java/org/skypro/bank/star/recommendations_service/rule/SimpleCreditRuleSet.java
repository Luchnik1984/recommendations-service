package org.skypro.bank.star.recommendations_service.rule;

import org.skypro.bank.star.recommendations_service.configuration.RecommendationRulesConfiguration;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepositoryImpl;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    private final UserDataRepositoryImpl userDataRepository;
    private final RecommendationRulesConfiguration rulesConfig;

    public SimpleCreditRuleSet(UserDataRepositoryImpl userDataRepository, RecommendationRulesConfiguration rulesConfig) {
        this.userDataRepository = userDataRepository;
        this.rulesConfig = rulesConfig;
    }

    @Override
    public Optional<RecommendationDTO> checkUser(UUID userId) {
        RecommendationRulesConfiguration.RuleConfig config = rulesConfig.getSimpleCreditConfig();

        boolean rule1 = !userDataRepository.hasProductType(userId, ProductType.CREDIT);

        boolean rule2 = userDataRepository.isDepositsGreaterThanWithdrawals(userId, ProductType.DEBIT);

        BigDecimal debitWithdrawals = userDataRepository.getTotalWithdrawalsAmount(userId, ProductType.DEBIT);
        boolean rule3 = debitWithdrawals.compareTo(config.getThreshold()) > 0;

        if (rule1 && rule2 && rule3) {
            return Optional.of(new RecommendationDTO(
                    config.getProductId(),
                    config.getProductName(),
                    config.getProductDescription()
            ));
        }

        return Optional.empty();
    }
}