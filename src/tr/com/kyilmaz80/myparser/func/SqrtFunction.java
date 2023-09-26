package tr.com.kyilmaz80.myparser.func;

public class SqrtFunction implements SingleArgMathFunction {
    private static String name = "sqrt";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double calculate(double arg) {
        return Math.sqrt(arg);
    }
}
