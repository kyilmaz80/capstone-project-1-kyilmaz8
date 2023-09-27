package tr.com.kyilmaz80.myparser.func;

public interface DoubleArgMathFunction extends MathFunction{
    double calculate(double arg1, double arg2);
    default int getArgCount() {
        return 2;
    }

}
