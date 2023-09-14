package func;
public class CosFunction implements SingleArgMathFunction {
    private static String name = "cos";
    @Override
    public String getName() {
            return name;
    }

    @Override
    public double calculate(double arg) {
        return Math.cos(arg);
    }
}
