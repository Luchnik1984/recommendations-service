package org.skypro.bank.star.recommendations_service.rule;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepositoryImpl;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500RuleSet implements RecommendationRuleSet {

    private static final UUID PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String PRODUCT_NAME = "Invest 500";
    private static final String PRODUCT_DESCRIPTION = "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!";

    private final UserDataRepositoryImpl userDataRepository;

    public Invest500RuleSet(UserDataRepositoryImpl userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public Optional<RecommendationDTO> checkUser(UUID userId) {

        /// Правило 1: Пользователь использует как минимум один продукт с типом DEBIT
        boolean rule1 = userDataRepository.hasProductType(userId, ProductType.DEBIT);

        /// Правило 2: Пользователь не использует продукты с типом INVEST
        boolean rule2 = !userDataRepository.hasProductType(userId, ProductType.INVEST);

        /// Правило 3: Сумма пополнений продуктов с типом SAVING больше 1000 ₽
        BigDecimal savingDeposits = userDataRepository.getTotalDepositsAmount(userId, ProductType.SAVING);
        boolean rule3 = savingDeposits.compareTo(new BigDecimal("1000")) > 0;

        if (rule1 && rule2 && rule3) {
            return Optional.of(new RecommendationDTO( PRODUCT_ID, PRODUCT_NAME,PRODUCT_DESCRIPTION));
        }

        return Optional.empty();
    }
}