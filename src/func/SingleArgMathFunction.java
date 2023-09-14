package func;

public interface SingleArgMathFunction extends MathFunction {
    public double calculate(double arg);
    public default int getArgCount() {
        return 1;
    }

}
