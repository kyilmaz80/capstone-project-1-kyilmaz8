package func;

public class TanFunction implements SingleArgMathFunction{
    @Override
    public String getName() {
        return "tan";
    }

    @Override
    public double calculate(double arg) {
        return Math.tan(arg);
    }
}
