import java.util.Arrays;

public class TokenUtils {

    public static boolean isTokenOperand(String str) {
        return isTokenNumerical(str) || isTokenMathFunction(str);
    }

    public static boolean isTokenNumerical(String str) {
        if (str.equals("")) {
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
        return Arrays.binarySearch(Constants.ALLOWED_MATH_FUNCTIONS, funcArray[0].toLowerCase()) >= 0;
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
}
