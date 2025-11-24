package org.skypro.bank.star.recommendations_service.rule.executor;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.QueryType;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserOfQueryExecutor implements RuleQueryExecutor{

    private final UserDataRepository userDataRepository;

    public UserOfQueryExecutor( UserDataRepository userDataRepository) {

        this.userDataRepository = userDataRepository;
    }

    @Override
    public boolean execute(UUID userId, List<String> arguments) {
        if (arguments.size() == 1) {
            return userDataRepository.hasProductType(userId, ProductType.valueOf(arguments.get(0)));
        } else throw new IllegalArgumentException(" Неверное количество аргументов ");
    }

    @Override
    public QueryType getSupportedQueryType() {
        return QueryType.USER_OF;
    }
}
