package org.skypro.bank.star.recommendations_service.model.dynamic;

import jakarta.persistence.*;
import org.skypro.bank.star.recommendations_service.enums.QueryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Сущность, представляющая один запрос в составе динамического правила.
 * Каждый RuleQuery определяет конкретную проверку, которая должна быть выполнена
 * для пользователя (например, проверка наличия продукта, сравнение сумм и т.д.).
 * Несколько RuleQuery объединяются в DynamicRule и выполняются последовательно
 * с логическим 'И' между ними.
 */

@Entity
@Table(name = "rule_queries")
public class RuleQuery {

    /**
     * Уникальный идентификатор запроса в базе данных.
     * Генерируется автоматически при сохранении.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тип запроса, определяющий логику проверки.
     * Сохраняется в БД как строка (enum value).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "query_type", nullable = false, length = 50)
    private QueryType query;

    /**
     * Список аргументов запроса.
     * Аргументы зависят от типа запроса и определяют конкретные параметры проверки
     * (типы продуктов, операции сравнения, пороговые значения и т.д.).
     * Хранится в отдельной таблице rule_query_arguments с сохранением порядка.
     */
    @ElementCollection
    @CollectionTable(
            name = "rule_query_arguments",
            joinColumns = @JoinColumn(name = "rule_query_id"),
            indexes = @Index(columnList = "rule_query_id")
    )
    @Column(name = "argument_value", nullable = false)
    @OrderColumn(name = "argument_order")
    private List<String> arguments = new ArrayList<>();

    /**
     * Флаг отрицания результата запроса.
     * Если true - результат запроса инвертируется перед использованием в правиле.
     * Пример: queryType=USER_OF, arguments=["CREDIT"], negate=true
     * означает "пользователь НЕ имеет кредитных продуктов"
     */
    @Column(nullable = false)
    private boolean negate = false;

    /**
     * Связь с родительским DynamicRule.
     * Обеспечивает каскадное сохранение и установку внешнего ключа.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dynamic_rule_id")
    private DynamicRule dynamicRule;


    public RuleQuery() {
    }

    /**
     * Конструктор для создания запроса с указанием всех параметров.
     * @param query тип запроса
     * @param arguments список аргументов запроса
     * @param negate флаг отрицания результата
     */
    public RuleQuery(QueryType query, List<String> arguments, boolean negate) {
        this.query = query;
        this.arguments = arguments != null ? new ArrayList<>(arguments) : new ArrayList<>();
        this.negate = negate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QueryType getQuery() {
        return query;
    }

    public void setQuery(QueryType queryType) {
        this.query = queryType;
    }

    /**
     * Возвращает список аргументов запроса.
     * @return список аргументов запроса
     */
    public List<String> getArguments() {
        return new ArrayList<>(arguments);
    }

    /**
     * Устанавливает список аргументов запроса.
     * Внутренний список заменяется копией переданного списка.
     * @param arguments список аргументов запроса
     */
    public void setArguments(List<String> arguments) {
        this.arguments = arguments!= null ? new ArrayList<>(arguments) : new ArrayList<>();
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    /**
     * Сравнивает данный объект с другим объектом на равенство.
     * Два RuleQuery считаются равными, если у них одинаковые queryType, arguments и negate.
     * Идентификатор id не учитывается при сравнении.
     * @param o объект для сравнения
     * @return true если объекты равны, false в противном случае
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleQuery ruleQuery = (RuleQuery) o;
        return negate == ruleQuery.negate &&
                query == ruleQuery.query &&
                Objects.equals(arguments, ruleQuery.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, arguments, negate);
    }

    @Override
    public String toString() {
        return "RuleQuery{" +
                "id=" + id +
                ", queryType=" + query +
                ", arguments=" + arguments +
                ", negate=" + negate +
                ", dynamicRule=" + (dynamicRule != null ? dynamicRule.getId() : "null") +
                '}';
    }
}
