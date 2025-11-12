package org.skypro.bank.star.recommendations_service.repository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.model.dto.RuleQueryDTO;

import java.util.List;

public class RuleQueryValidator implements ConstraintValidator<RuleQueryValid, RuleQueryDTO> {

    @Override
    public boolean isValid(RuleQueryDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.query() == null || dto.arguments() == null)
            return false;

        String[] args = dto.arguments();

        try {
            return switch (dto.query()) {
                case USER_OF, ACTIVE_USER_OF ->
                        args.length == 1 && isProductType(args[0]);

                case TRANSACTION_SUM_COMPARE ->
                        args.length == 4 &&
                                isProductType(args[0]) &&
                                isTransactionType(args[1]) &&
                                isComparativeType(args[2]) &&
                                isNonNegativeInt(args[3]);

                case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW ->
                        args.length == 2 &&
                                isProductType(args[0]) &&
                                isComparativeType(args[1]);
            };
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isProductType(String value) {
        try {
            ProductType.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTransactionType(String value) {
        try {
            TransactionType.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isComparativeType(String value) {
        try {
            ComparativeType.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isNonNegativeInt(String value) {
        try {
            return Integer.parseInt(value) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}