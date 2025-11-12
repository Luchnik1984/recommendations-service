package org.skypro.bank.star.recommendations_service.enums;


/**
 * Перечисление операторов сравнения для правил рекомендаций.
 * Поддерживает пять операторов сравнения.
 * Каждый оператор имеет символьное представление и логику сравнения чисел.
 *
 * @see ComparativeType#compare(double, double)
 */
public enum ComparativeType {

    /**
     * Сумма строго больше числа C.
     * * Символ: ">"
     */
    GREATER(">"),

    /**
     * Сумма строго меньше числа C.
     * Символ: "<"
     */
    LESS("<"),

    /**
     * Сумма больше или равна числу C.
     * Символ: "="
     */
    EQUAL("="),

    /**
     * Сумма меньше или равна числу C.
     * Символ: ">="
     */
    GREATER_OR_EQUAL(">="),

    /**
     * Сумма больше или равна числу C.
     * Символ: "<="
     */
    LESS_OR_EQUAL("<=");

    private final String symbol;

    /**
     * Конструктор для создания оператора сравнения с указанным символом.
     *
     * @param symbol строковое представление оператора
     */
    ComparativeType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Выполняет сравнение двух чисел согласно логике оператора.
     *
     * @param a первое число для сравнения
     * @param b второе число для сравнения
     * @return true если условие сравнения выполняется, false в противном случае
     */
    public boolean compare(double a, double b) {
        return switch (this) {
            case GREATER -> a > b;
            case LESS -> a < b;
            case EQUAL -> a == b;
            case GREATER_OR_EQUAL -> a >= b;
            case LESS_OR_EQUAL -> a <= b;
        };
    }

    /**
     * Преобразует строковое представление оператора в соответствующий enum.
     * Поддерживает все пять операторов, указанных в требованиях части 2.
     *
     * @param symbol строковое представление оператора (">", "<", "=", ">=", "<=")
     * @return соответствующий ComparativeType
     * @throws IllegalArgumentException если передан неизвестный оператор
     */
    public static ComparativeType fromString(String symbol) {
        for (ComparativeType type : ComparativeType.values()) {
            if (type.symbol.equals(symbol)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + symbol);
    }
}

