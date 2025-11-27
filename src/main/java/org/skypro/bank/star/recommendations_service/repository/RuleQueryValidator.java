package org.skypro.bank.star.recommendations_service.repository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.skypro.bank.star.recommendations_service.enums.ComparativeType;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.model.dto.RuleQueryDTO;


/**
 * Валидатор для проверки корректности RuleQueryDTO.
 * Осуществляет проверку соответствия аргументов типу запроса согласно бизнес-требованиям.
 *
 * @see RuleQueryValid
 * @see RuleQueryDTO
 */
public class RuleQueryValidator implements ConstraintValidator<RuleQueryValid, RuleQueryDTO> {

    /**
     * Проверяет валидность RuleQueryDTO согласно бизнес-правилам.
     *
     * @param dto     объект RuleQueryDTO для валидации
     * @param context контекст валидации
     * @return true если DTO валиден, false в противном случае
     */
    @Override
    public boolean isValid(RuleQueryDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.query() == null || dto.arguments() == null)
            return false;

        String[] args = dto.arguments();

        try {
            return switch (dto.query()) {
                case USER_OF, ACTIVE_USER_OF -> args.length == 1 && isProductType(args[0]);

                case TRANSACTION_SUM_COMPARE -> args.length == 4 &&
                        isProductType(args[0]) &&
                        isTransactionType(args[1]) &&
                        isComparativeType(args[2]) &&
                        isNonNegativeInt(args[3]);

                case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW -> args.length == 2 &&
                        isProductType(args[0]) &&
                        isComparativeType(args[1]);
            };
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Проверяет, является ли значение валидным типом продукта.
     *
     * @param value строковое значение для проверки
     * @return true если значение соответствует одному из значений ProductType
     */
    private boolean isProductType(String value) {
        try {
            ProductType.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Проверяет, является ли значение валидным типом транзакции.
     *
     * @param value строковое значение для проверки
     * @return true если значение соответствует одному из значений TransactionType
     */
    private boolean isTransactionType(String value) {
        try {
            TransactionType.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Проверяет, является ли значение валидным оператором сравнения.
     *
     * @param value строковое значение для проверки
     * @return true если значение соответствует одному из поддерживаемых операторов сравнения
     */
    private boolean isComparativeType(String value) {
        try {
            ComparativeType.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Проверяет, является ли значение неотрицательным целым числом в диапазоне int.
     * Соответствует требованию: "неотрицательное целое число, которое умещается в тип int"
     *
     * @param value строковое значение для проверки
     * @return true если значение является неотрицательным целым числом от 0 до Integer.MAX_VALUE
     */
    private boolean isNonNegativeInt(String value) {
        try {
            return Integer.parseInt(value) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}