package tr.com.kyilmaz80.myparser.func;

public interface MultiArgMathFunction extends MathFunction{

    public double calculate(Double[] args);

    @Override
    default int getArgCount() {
        return -1;
    }

    public void setArgCount(int argCount);
}
