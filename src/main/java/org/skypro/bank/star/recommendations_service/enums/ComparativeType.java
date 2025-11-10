package org.skypro.bank.star.recommendations_service.enums;


public enum ComparativeType {

    /**
     * Сумма строго больше числа C.
     */
    GREATER(">"),

    /**
     * Сумма строго меньше числа C.
     */
    LESS("<"),

    /**
     * Сумма больше или равна числу C.
     */
    EQUAL("="),

    /**
     * Сумма меньше или равна числу C.
     */
    GREATER_OR_EQUAL(">="),

    /**
     * Сумма больше или равна числу C.
     */
    LESS_OR_EQUAL("<=");

    private final String symbol;

    ComparativeType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }


    /**
     *  Метод для сравнения чисел
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
     * Получение enum из строки
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

