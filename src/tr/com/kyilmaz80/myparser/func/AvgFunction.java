package tr.com.kyilmaz80.myparser.func;

public class AvgFunction implements DoubleArgMathFunction, MultiArgMathFunction{

    public static String name = "avg";
    public int argCount = -1;
    @Override
    public double calculate(double arg1, double arg2) {
        return Double.sum(arg1, arg2)/2;
    }

    @Override
    public int getArgCount() {
        return argCount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double calculate(Double[] args) {
        double sum = 0;
        for(int i = 0; i < args.length; i++ ) {
            sum += args[i];
        }
        return sum/args.length;
    }

    @Override
    public void setArgCount(int argCount) {
        this.argCount = argCount;
    }
}
