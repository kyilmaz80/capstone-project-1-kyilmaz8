package func;

public class SinFunction implements SingleArgMathFunction {
    private static String name = "sin";
    @Override
    public String getName() {
        return name;
    }

    @Override
    public double calculate(double arg) {
        return Math.sin(arg);
    }
}
