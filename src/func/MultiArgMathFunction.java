package func;

public interface MultiArgMathFunction extends MathFunction{

    public double calculate(Double[] args);

    @Override
    default int getArgCount() {
        return -1;
    }
}
