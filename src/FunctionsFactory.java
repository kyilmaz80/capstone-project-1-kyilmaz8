import func.*;
public final class FunctionsFactory {
    private static FlexCalculator ff;

    private  FunctionsFactory() {
    }

    public static FlexCalculator getInstance() {
        if (ff == null) {
            ff = new FlexCalculator(FunctionConstants.ALLOWED_MATH_FUNCTIONS.length);
        }
        return ff;
    }

}
