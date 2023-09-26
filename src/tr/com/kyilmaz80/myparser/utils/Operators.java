package tr.com.kyilmaz80.myparser.utils;

public enum Operators {
    LEFT_PARENTHESES("(", -1),
    RIGHT_PARENTHESES(")", -1),
    ADDITION("+",1),
    SUBTRACTION("-", 1),
    MULTIPLICATION("*", 2),
    DIVISION("/", 2),
    FUNC_VARIABLE_COMMA(",", -2);

    private final String symbol;
    private final int priority;

    Operators(String symbol, int priority) {
        this.symbol = symbol;
        this.priority = priority;
    }

    public boolean isOperatorHighestPriorityFrom(Operators op) {
        if (op == null) {
            return false;
        }
        if (op.equals(Operators.MULTIPLICATION) && this.equals(Operators.DIVISION)) {
            return false;
        } else if (op.equals(Operators.ADDITION) && this.equals(Operators.SUBTRACTION)) {
            return false;
        } else {
            return compareTo(op) > 0;
        }
    }

    public boolean isOperatorSamePriorityTo(Operators op) {
        return op.priority == this.priority;
    }
    public static Operators fromSymbol(String symbol) {
        for (Operators op : Operators.values()) {
            if (op.symbol.equalsIgnoreCase(symbol)) {
                return op;
            }
        }
        return null;
    }

}
