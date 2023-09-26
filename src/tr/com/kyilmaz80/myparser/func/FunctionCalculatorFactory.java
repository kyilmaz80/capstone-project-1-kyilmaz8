package tr.com.kyilmaz80.myparser.func;

public final class FunctionCalculatorFactory {
    private static FunctionCalculator ff;

    private FunctionCalculatorFactory() {
    }

    public static FunctionCalculator getInstance() {
        if (ff == null) {
            ff = new FunctionCalculator(FunctionConstants.ALLOWED_MATH_FUNCTIONS.length);
        }
        return ff;
    }

}
