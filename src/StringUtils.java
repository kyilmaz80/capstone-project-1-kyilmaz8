public class StringUtils {
    public static String removeSpaces(String str) {
        return str.replace(Constants.WHITESPACE, Constants.BLANK);
    }

    private static String appendWhiteSpaceToPostfixExpression(String exp) {
        return exp.concat(Constants.WHITESPACE);
    }

    public static boolean isStringContainsParentheses(String str) {
        return str.contains(Constants.LEFT_PARENTHESES) || str.contains(Constants.RIGHT_PARENTHESES);
    }

    public static void doAppendOperatorToPostfixExpression(StringBuilder sb, String element) {
        String str = sb.toString().trim();
        int lastIndex = str.length()-1;
        String last = String.valueOf(str.charAt(lastIndex));
        Operators opNew = Operators.fromSymbol(element.trim());
        Operators opLast = Operators.fromSymbol(last);
        //TODO: other cases
        sb.append(element);
    }
}
