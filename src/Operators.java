public enum Operators {
    ADDITION("+",1),
    SUBTRACTION("-", 1),
    MULTIPLICATION("*", 2),
    DIVISION("/", 2);

    private final String symbol;
    private final int no;

    Operators(String symbol, int no) {
        this.symbol = symbol;
        this.no = no;
    }

    public boolean isOperatorHighestPriorityFrom(Operators op) {

        if (op.equals(Operators.MULTIPLICATION) && this.equals(Operators.DIVISION)) {
            return false;
        } else if (op.equals(Operators.ADDITION) && this.equals(Operators.SUBTRACTION)) {
            return false;
        } else {
            return compareTo(op) > 0;
        }
    }

    public boolean isOperatorSamePriorityTo(Operators op) {
        return op.no == this.no;
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
