package func;

public class PowFunction implements DoubleArgMathFunction{
    @Override
    public double calculate(double arg1, double arg2) {
        return Math.pow(arg2, arg1);
    }

    @Override
    public String getName() {
        return "pow";
    }

    @Override
    public int getArgCount() {
        return 2;
    }
}
