package tr.com.kyilmaz80.myparser.utils;

import java.util.Arrays;
import tr.com.kyilmaz80.myparser.func.FunctionConstants;
public class TokenUtils {

    public static boolean isTokenOperand(String str) {
        return isTokenNumerical(str) || isTokenMathFunction(str);
    }

    public static boolean isTokenNumerical(String str) {
        if (str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isTokenMathFunction(String str) {
        if (str == null || str.length() <= 1) {
            return false;
        }
        String[] funcArray = str.split("\\(");
        //multiarg with variable count functions also func
        //max3 -> max
        String functionName = StringUtils.removeNumbers(funcArray[0]).toLowerCase();
        Arrays.sort(FunctionConstants.ALLOWED_MATH_FUNCTIONS);
        return Arrays.binarySearch(FunctionConstants.ALLOWED_MATH_FUNCTIONS, functionName) >= 0;
    }

    public static boolean isTokenValid(String token) {
        return isTokenExit(token) || isTokenNumerical(token) || isTokenMathFunction(token) || isTokenDelimiter(token);
    }

    public static boolean isTokenExit(String token) {
        return token.equalsIgnoreCase(Constants.COMMAND_EXIT);
    }

    public static String filterToken(String token) {
        return token.trim();
    }

    public static boolean isTokenDelimiter(String token) {
        return Constants.DELIMITERS.contains(token);
    }

    public static boolean isTokenArithmeticOperator(String token) {
        Operators op = Operators.fromSymbol(token);
        return op != null;
    }

}
