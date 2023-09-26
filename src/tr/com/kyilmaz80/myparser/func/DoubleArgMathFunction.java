package tr.com.kyilmaz80.myparser.func;

public interface DoubleArgMathFunction extends MathFunction{
    public double calculate(double arg1, double arg2);
    public default int getArgCount() {
        return 2;
    }

}
