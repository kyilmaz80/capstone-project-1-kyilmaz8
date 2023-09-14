package func;

public interface Calculator {
    public double doCalculation(String functionName, double arg);

    public double doCalculation(String functionName, double arg1, double arg2);

    public void addFunction(MathFunction function);

    public void listMathFunction();
}
