public class StringUtils {
    public static String removeSpaces(String str) {
        return str.replace(Constants.WHITESPACE, Constants.BLANK);
    }

    private static String appendWhiteSpaceToPostfixExpression(String exp) {
        return exp.concat(Constants.WHITESPACE);
    }
}
