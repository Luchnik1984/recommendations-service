package org.skypro.bank.star.recommendations_service.rule;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepositoryImpl;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    private static final UUID PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final String PRODUCT_NAME = "Простой кредит";
    private static final String PRODUCT_DESCRIPTION = "Откройте мир выгодных кредитов с нами!\nИщете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\nПочему выбирают нас: Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\nУдобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\nШирокий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\nНе упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!";

    private final UserDataRepositoryImpl userDataRepository;

    public SimpleCreditRuleSet(UserDataRepositoryImpl userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public Optional<RecommendationDTO> checkUser(UUID userId) {

        /// Правило 1: Пользователь не использует продукты с типом CREDIT
        boolean rule1 = !userDataRepository.hasProductType(userId, ProductType.CREDIT);

        /// Правило 2: Сумма пополнений по всем продуктам типа DEBIT больше, чем сумма трат
        boolean rule2 = userDataRepository.isDepositsGreaterThanWithdrawals(userId, ProductType.DEBIT);

        /// Правило 3: Сумма трат по всем продуктам типа DEBIT больше, чем 100,000 ₽
        BigDecimal debitSpends = userDataRepository.getTotalWithdrawalsAmount(userId, ProductType.DEBIT);
        boolean rule3 = debitSpends.compareTo(new BigDecimal("100000")) > 0;

        if (rule1 && rule2 && rule3) {
            return Optional.of(new RecommendationDTO( PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION));
        }

        return Optional.empty();
    }
}