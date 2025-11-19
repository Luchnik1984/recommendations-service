package org.skypro.bank.star.recommendations_service.rule;

import org.skypro.bank.star.recommendations_service.configuration.RecommendationRulesConfiguration;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    private final UserDataRepository userDataRepository;
    private final RecommendationRulesConfiguration rulesConfig;

    public SimpleCreditRuleSet(@Qualifier("CachedUserDataRepository") UserDataRepository userDataRepository
            , RecommendationRulesConfiguration rulesConfig) {
        this.userDataRepository = userDataRepository;
        this.rulesConfig = rulesConfig;
    }

    @Override
    public Optional<RecommendationDTO> checkUser(UUID userId) {
        RecommendationRulesConfiguration.RuleConfig config = rulesConfig.getSimpleCreditConfig();

        boolean rule1 = !userDataRepository.hasProductType(userId, ProductType.CREDIT);

        if (!rule1) return Optional.empty();

        boolean rule2 = userDataRepository.isDepositsGreaterThanWithdrawals(userId, ProductType.DEBIT);

        if (!rule2) return Optional.empty();

        BigDecimal debitWithdrawals = userDataRepository.getTotalAmount(
                userId,
                ProductType.DEBIT,
                TransactionType.WITHDRAW);

        boolean rule3 = debitWithdrawals.compareTo(config.getThreshold()) > 0;

        if (!rule3) return Optional.empty();

        return Optional.of(new RecommendationDTO(
                config.getProductId(),
                config.getProductName(),
                config.getProductDescription()));
    }
}