package org.skypro.bank.star.recommendations_service.rule.executor;

import org.skypro.bank.star.recommendations_service.enums.ComparativeType;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.QueryType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepositoryImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TransactionSumCompareDepositWithdrawQueryExecutor implements RuleQueryExecutor {

    private final UserDataRepositoryImpl userDataRepository;

    public TransactionSumCompareDepositWithdrawQueryExecutor(UserDataRepositoryImpl userDataRepository) {
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
