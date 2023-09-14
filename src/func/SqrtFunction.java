package func;

public class SqrtFunction implements SingleArgMathFunction {
    @Override
    public String getName() {
        return "sqrt";
    }

    @Override
    public double calculate(double arg) {
        return Math.sqrt(arg);
    }
}
