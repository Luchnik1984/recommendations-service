package org.skypro.bank.star.recommendations_service.rule.executor;

import org.skypro.bank.star.recommendations_service.enums.QueryType;

import java.util.List;
import java.util.UUID;

/**
 * Абстрактный базовый класс для всех исполнителей запросов правил рекомендаций.
 * Определяет общий интерфейс и базовую функциональность для выполнения
 * различных типов проверок пользовательских данных.
 * Каждый конкретный исполнитель должен реализовать метод execute() для
 * выполнения специфичной для типа запроса логики проверки.
 */
public abstract class RuleQueryExecutor {

    /**
     * Выполняет запрос для указанного пользователя с заданными аргументами.
     * @param userId UUID пользователя, для которого выполняется проверка
     * @param arguments список аргументов запроса, специфичных для типа запроса
     * @return true если условие запроса выполнено, false в противном случае
     * @throws IllegalArgumentException если аргументы не соответствуют ожидаемому формату
     * @throws RuntimeException если произошла ошибка при выполнении запроса к БД
     */
    public abstract boolean execute(UUID userId, List<String> arguments);

    /**
     * Возвращает тип запроса, который обрабатывает данный исполнитель.
     * Каждый исполнитель должен поддерживать ровно один тип запроса.
     * @return тип запроса, поддерживаемый исполнителем
     */
    public abstract QueryType getSupportedQueryType();

    /**
     * Валидирует количество аргументов запроса.
     * Выбрасывает IllegalArgumentException если количество аргументов не соответствует ожидаемому.
     * @param arguments список аргументов для валидации
     * @param expectedCount ожидаемое количество аргументов
     * @throws IllegalArgumentException если количество аргументов не соответствует expectedCount
     */
    protected void validateArguments(List<String> arguments, int expectedCount) {
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments list cannot be null");
        }
        if (arguments.size() != expectedCount) {
            throw new IllegalArgumentException(
                    String.format("Expected %d arguments for query type %s, but got %d: %s",
                            expectedCount, getSupportedQueryType(), arguments.size(), arguments)
            );
        }
    }

    /**
     * Валидирует что список аргументов не пуст.
     * @param arguments список аргументов для валидации
     * @throws IllegalArgumentException если список аргументов пуст
     */
    protected void validateArgumentsNotEmpty(List<String> arguments) {
        if (arguments == null || arguments.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Arguments list cannot be empty for query type %s",
                            getSupportedQueryType())
            );
        }
    }

    /**
     * Возвращает строковое представление исполнителя.
     * @return строковое представление в формате "RuleQueryExecutor{supportedQueryType=...}"
     */
    @Override
    public String toString() {
        return "RuleQueryExecutor{supportedQueryType=" + getSupportedQueryType() + "}";
    }
}
