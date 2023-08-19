public enum Operators {
    ADDITION("+",1),
    SUBTRACTION("-", 2),
    MULTIPLICATION("*", 3),
    DIVISION("/", 4);

    private String symbol;
    private int no;

    Operators(String symbol, int no) {
        this.symbol = symbol;
        this.no = no;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public boolean isOperatorPrior(Operators op) {
        return compareTo(op) > 0;
    }
}
