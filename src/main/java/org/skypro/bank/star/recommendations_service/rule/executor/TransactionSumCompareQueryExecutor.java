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
public class TransactionSumCompareQueryExecutor implements RuleQueryExecutor {

    private final UserDataRepository userDataRepository;

    public TransactionSumCompareQueryExecutor(
            @Qualifier("CachedUserDataRepository") UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public boolean execute(UUID userId, List<String> arguments) {
        if (arguments.size() == 4) {
            ProductType productType = ProductType.valueOf(arguments.get(0));
            TransactionType transactionType = TransactionType.valueOf(arguments.get(1));
            ComparativeType operator = ComparativeType.fromString(arguments.get(2));
            double amount = Double.parseDouble(arguments.get(3));

            double transactionSum = userDataRepository.getTransactionSumByType(userId, productType, transactionType);

            return operator.compare(transactionSum, amount);
        } else {
            throw new IllegalArgumentException("Неверное количество аргументов");
        }
    }

    @Override
    public QueryType getSupportedQueryType() {
        return QueryType.TRANSACTION_SUM_COMPARE;
    }
}
