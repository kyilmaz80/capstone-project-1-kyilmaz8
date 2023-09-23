package func;

import java.util.Arrays;

public class MaxFunction implements MultiArgMathFunction {

    @Override
    public String getName() {
        return "max";
    }

    @Override
    public double calculate(Double[] args) {
        Arrays.sort(args);
        return args[args.length - 1];
    }

    
}
