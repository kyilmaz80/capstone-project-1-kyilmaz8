package tr.com.kyilmaz80.myparser.utils;

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
        int lastIndex = str.length() - 1;
        String last = String.valueOf(str.charAt(lastIndex));
        Operators opNew = Operators.fromSymbol(element.trim());
        Operators opLast = Operators.fromSymbol(last);
        //TODO: other cases
        sb.append(element);
    }

    public static void doReplaceToPostfixExpression(StringBuilder sb, int argCount) {
        String str = sb.toString().trim();
        String[] arr = str.split(Constants.WHITESPACE);
        String funcNameToFind = arr[arr.length - (argCount + 1)];
        String newFuncName = funcNameToFind + argCount;
        int funcIndex = sb.indexOf(funcNameToFind);
        sb.replace(funcIndex, funcIndex + funcNameToFind.length(), newFuncName);
    }
    public static String removeNumbers(String str) {
        return str.replaceAll("\\d", Constants.BLANK);
    }
}