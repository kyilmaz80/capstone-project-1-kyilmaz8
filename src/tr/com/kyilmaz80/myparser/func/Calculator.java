package tr.com.kyilmaz80.myparser.func;

public interface Calculator {
    double doCalculation(String functionName, double arg);

    double doCalculation(String functionName, double arg1, double arg2);

    Double doCalculation(String functionName, Double[] args);

    void addFunction(MathFunction function);

    MathFunction[] getFunctions();

    void listMathFunction();
}
