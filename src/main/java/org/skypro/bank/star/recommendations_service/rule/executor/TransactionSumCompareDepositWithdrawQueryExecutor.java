package org.skypro.bank.star.recommendations_service.rule.executor;

import org.skypro.bank.star.recommendations_service.enums.ComparativeType;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.QueryType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TransactionSumCompareDepositWithdrawQueryExecutor implements RuleQueryExecutor {

    private final UserDataRepository userDataRepository;

    public TransactionSumCompareDepositWithdrawQueryExecutor(
            @Qualifier("CachedUserDataRepository") UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public boolean execute(UUID userId, List<String> arguments) {
        if (arguments.size() == 2) {
            ProductType productType = ProductType.valueOf(arguments.get(0));
            ComparativeType operator = ComparativeType.fromString(arguments.get(1));

            double depositSum = userDataRepository.getTransactionSumByType(userId, productType, TransactionType.DEPOSIT);
            double withdrawSum = userDataRepository.getTransactionSumByType(userId, productType, TransactionType.WITHDRAW);

            return operator.compare(depositSum, withdrawSum);
        } else {
            throw new IllegalArgumentException("Неверное количество аргументов");
        }
    }

    @Override
    public QueryType getSupportedQueryType() {
        return QueryType.TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW;
    }
}
