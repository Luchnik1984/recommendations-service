package org.skypro.bank.star.recommendations_service.configuration;

import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Конфигурация всех бизнес-правил рекомендаций
 * Все константы вынесены в один класс
 */
@Configuration
public class RecommendationRulesConfiguration {

    public static final UUID INVEST_500_PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    public static final String INVEST_500_PRODUCT_NAME = "Invest 500";
    public static final BigDecimal INVEST_500_SAVING_MIN = BigDecimal.valueOf(1_000);
    public static final String INVEST_500_DESCRIPTION =
            """
                      Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка!\
                      Воспользуйтесь налоговыми льготами и начните инвестировать с умом.\
                      Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде.\
                      Не упустите возможность разнообразить свой портфель,
                      снизить риски и следить за актуальными рыночными тенденциями.\
                      Откройте ИИС сегодня и станьте ближе к финансовой независимости!
                    """;

    public static final UUID SIMPLE_CREDIT_PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    public static final String SIMPLE_CREDIT_PRODUCT_NAME = "Простой кредит";
    public static final BigDecimal SIMPLE_CREDIT_MIN_WITHDRAWALS = BigDecimal.valueOf(100_000);
    public static final String SIMPLE_CREDIT_DESCRIPTION =
            """
                    Откройте мир выгодных кредитов с нами!\
                    
                    Ищете способ быстро и без лишних хлопот получить нужную сумму?\
                    Тогда наш выгодный кредит — именно то, что вам нужно!\
                    Мы предлагаем низкие процентные ставки,
                    гибкие условия и индивидуальный подход к каждому клиенту.\
                    
                    Почему выбирают нас: Быстрое рассмотрение заявки.\
                    Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\
                    
                    Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\
                    
                    Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости,
                    автомобиля, образование, лечение и многое другое.\
                    
                    Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!
                    """;

    public static final UUID TOP_SAVING_PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    public static final String TOP_SAVING_PRODUCT_NAME = "Top Saving";
    public static final BigDecimal TOP_SAVING_DEBIT_LIMIT = BigDecimal.valueOf(50_000);
    public static final BigDecimal TOP_SAVING_SAVING_LIMIT = BigDecimal.valueOf(50_000);
    public static final String TOP_SAVING_DESCRIPTION =
            """
                     Откройте свою собственную «Копилку» с нашим банком!\
                    
                     «Копилка» — это уникальный банковский инструмент,
                     который поможет вам легко и удобно накапливать деньги на важные цели.\
                     Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\
                    
                     Преимущества «Копилки»: Накопление средств на конкретные цели.\
                     Установите лимит и срок накопления,  и банк будет автоматически переводить определенную сумму на ваш счет.\
                     Прозрачность и контроль. Отслеживайте свои доходы и расходы,
                     контролируйте процесс накопления и корректируйте стратегию при необходимости.\
                    
                     Безопасность и надежность. Ваши средства находятся под защитой банка,
                     а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\
                    
                     Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!\
                    """;

    /**
     * DTO для передачи конфигурации в правила
     */
    public static class RuleConfig {
        private final UUID productId;
        private final String productName;
        private final String productDescription;
        private final BigDecimal threshold;
        private final BigDecimal threshold2; // для правил с двумя порогами

        public RuleConfig(UUID productId, String productName, String productDescription, BigDecimal threshold) {
            this.productId = productId;
            this.productName = productName;
            this.productDescription = productDescription;
            this.threshold = threshold;
            this.threshold2 = null;
        }

        public RuleConfig(UUID productId, String productName, String productDescription,
                          BigDecimal threshold1, BigDecimal threshold2) {
            this.productId = productId;
            this.productName = productName;
            this.productDescription = productDescription;
            this.threshold = threshold1;
            this.threshold2 = threshold2;
        }

        public UUID getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public String getProductDescription() {
            return productDescription;
        }

        public BigDecimal getThreshold() {
            return threshold;
        }

        public BigDecimal getThreshold2() {
            return threshold2;
        }
    }

    /**
     * Метод для получения конфигурации правила Invest500
     */

    public RuleConfig getInvest500Config() {
        return new RuleConfig(
                INVEST_500_PRODUCT_ID,
                INVEST_500_PRODUCT_NAME,
                INVEST_500_DESCRIPTION,
                INVEST_500_SAVING_MIN
        );
    }

    /**
     * Метод для получения конфигурации правила SimpleCredit
     */

    public RuleConfig getSimpleCreditConfig() {
        return new RuleConfig(
                SIMPLE_CREDIT_PRODUCT_ID,
                SIMPLE_CREDIT_PRODUCT_NAME,
                SIMPLE_CREDIT_DESCRIPTION,
                SIMPLE_CREDIT_MIN_WITHDRAWALS
        );
    }

    /**
     * Метод для получения конфигурации правила TopSaving
     */

    public RuleConfig getTopSavingConfig() {
        return new RuleConfig(
                TOP_SAVING_PRODUCT_ID,
                TOP_SAVING_PRODUCT_NAME,
                TOP_SAVING_DESCRIPTION,
                TOP_SAVING_DEBIT_LIMIT,
                TOP_SAVING_SAVING_LIMIT
        );
    }
}
