package org.skypro.bank.star.recommendations_service.rule;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepositoryImpl;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRuleSet implements RecommendationRuleSet {

    private static final UUID PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private static final String PRODUCT_NAME = "Top Saving";
    private static final String PRODUCT_DESCRIPTION = "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\nПреимущества «Копилки»: Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.\nПрозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.\nБезопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\nНачните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!";
    private double  totalDebitReplenishment = 50_000;
    private double totalSavingReplenishment = 50_000;

    private final UserDataRepositoryImpl userDataRepository;

    public TopSavingRuleSet(UserDataRepositoryImpl userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public Optional<RecommendationDTO> checkUser(UUID userId) {

        /// Правило 1: Пользователь использует как минимум один продукт с типом DEBIT
        boolean rule1 = userDataRepository.hasProductType(userId, ProductType.DEBIT);

        /// Правило 2: Сумма пополнений по всем продуктам типа DEBIT >= 50,000 ₽ ИЛИ
        /// сумма пополнений по всем продуктам типа SAVING >= 50,000 ₽
        BigDecimal debitDeposits = userDataRepository.getTotalDepositsAmount(userId, ProductType.DEBIT);
        BigDecimal savingDeposits = userDataRepository.getTotalDepositsAmount(userId, ProductType.SAVING);
        boolean rule2 = debitDeposits.compareTo(new BigDecimal(totalDebitReplenishment)) >= 0 ||
                        savingDeposits.compareTo(new BigDecimal(totalSavingReplenishment)) >= 0;

        /// Правило 3: Сумма пополнений по всем продуктам типа DEBIT больше, чем сумма трат
        boolean rule3 = userDataRepository.isDepositsGreaterThanWithdrawals(userId, ProductType.DEBIT);

        if (rule1 && rule2 && rule3) {
            return Optional.of(new RecommendationDTO( PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION));
        }

        return Optional.empty();
    }
}