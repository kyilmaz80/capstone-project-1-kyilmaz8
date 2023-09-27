package tr.com.kyilmaz80.myparser.utils;

public class StringUtils {
    public static String removeSpaces(String str) {
        return str.replace(Constants.WHITESPACE, Constants.BLANK);
    }

    public static boolean isStringContainsParentheses(String str) {
        return str.contains(Constants.LEFT_PARENTHESES) || str.contains(Constants.RIGHT_PARENTHESES);
    }

    public static void doAppendOperatorToPostfixExpression(StringBuilder sb, String element) {
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