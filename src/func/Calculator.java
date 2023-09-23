package func;

public interface Calculator {
    public double doCalculation(String functionName, double arg);

    public double doCalculation(String functionName, double arg1, double arg2);

    public Double doCalculation(String functionName, Double[] args);

    public void addFunction(MathFunction function);

    public MathFunction[] getFunctions();

    public void listMathFunction();
}
