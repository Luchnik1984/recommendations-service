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
public class TopSavingRuleSet implements RecommendationRuleSet {

    private final UserDataRepositoryImpl userDataRepository;
    private final RecommendationRulesConfiguration rulesConfig;

    public TopSavingRuleSet(UserDataRepositoryImpl userDataRepository, RecommendationRulesConfiguration rulesConfig) {
        this.userDataRepository = userDataRepository;
        this.rulesConfig = rulesConfig;
    }

    @Override
    public Optional<RecommendationDTO> checkUser(UUID userId) {
        RecommendationRulesConfiguration.RuleConfig config = rulesConfig.getTopSavingConfig();

        boolean rule1 = userDataRepository.hasProductType(userId, ProductType.DEBIT);

        BigDecimal debitDeposits = userDataRepository.getTotalDepositsAmount(userId, ProductType.DEBIT);
        BigDecimal savingDeposits = userDataRepository.getTotalDepositsAmount(userId, ProductType.SAVING);
        boolean rule2 = debitDeposits.compareTo(config.getThreshold()) >= 0 ||
                savingDeposits.compareTo(config.getThreshold2()) >= 0;

        boolean rule3 = userDataRepository.isDepositsGreaterThanWithdrawals(userId, ProductType.DEBIT);

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