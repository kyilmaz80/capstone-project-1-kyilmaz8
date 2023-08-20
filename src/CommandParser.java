import java.util.Arrays;
import java.util.Stack;
import java.util.StringTokenizer;

public class CommandParser {
    //private static Stack operatorsStack = new Stack();
    //private static Stack operandsStack = new Stack();
    public static String parse(String command) {
        /*
        StringTokenizer st = new StringTokenizer(command, Constants.DELIMITERS, true);
        while(st.hasMoreElements()) {
            String tokenString = filterToken(st.nextToken());
            if (isTokenNumerical(tokenString)) {
                operandsStack.push(tokenString);
            } else {
                operatorsStack.push(tokenString);
            }
            if (!isTokenValid(filterToken(tokenString))) {
                return null;
            }
        }
        */
        return convertToPostfixExpression(command);
    }

    public static double execute(String command) {
        //TODO: komutun parse edilmis halinin hesabi
        String op;
        double result = 0;

        return result;
    }

    private static String convertToPostfixExpression(String infixExpression) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(infixExpression, Constants.DELIMITERS, true);
        Stack<String> stack = new Stack<String>();
        while(st.hasMoreElements()) {
            String tokenString = filterToken(st.nextToken());
            if (isTokenOperand(tokenString)) {
                sb.append(tokenString);
            } else {
                // token is operator
                if (stack.isEmpty()) {
                    stack.push(tokenString);
                } else {
                    //check top element
                    //rule highest priority operators like to be on top
                    //^ -> highest, */ -> next priority, +- lowest priority
                    //no two operator of same priority can stay together
                    //pop the top from stack to postfix, then push item
                    String opOnStack = stack.pop().toString();
                    Operators opOnStackOperator = Operators.fromSymbol(opOnStack);
                    Operators tokenOperator = Operators.fromSymbol(tokenString);
                    if (opOnStackOperator.isOperatorHighestPriorityFrom(tokenOperator) ||
                        opOnStackOperator.isOperatorSamePriorityTo(tokenOperator)) {
                        //rule: highest priority must be on top!
                        //we have to pop from stack to postfix expression
                        //then push it
                        //since we already popped it
                        //rule: no same priority operators stay together
                        sb.append(tokenString);

                    } else {
                        stack.push(opOnStack);
                        //push back the popped element
                    }
                    stack.push(tokenString);
                }
            }
        }
        // add the operators to postfix expression
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }

        System.out.println(sb.toString());

        return sb.toString();
    }
    private static double doOperation(double val1, double val2, String op) {
        double result = -1;
        if (op.equals("*")) {
            result = val2 * val1;
        } else if (op.equals("/")) {
            result = val2 / val1;
        } else if (op.equals("+")) {
            result = val2 + val1;
        } else if (op.equals("-")) {
            result = val1 - val2;
        } else {
            //TODO: pass
            System.out.println("NOT IMPLEMENTED!");
        }
        return result;
    }

    private static boolean isTokenOperand(String str) {
        return isTokenNumerical(str) || isTokenMathFunction(str);
    }
    private static boolean isTokenNumerical(String str) {
        if (str.equals("") || str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static boolean isTokenMathFunction(String str) {
        String[] funcArray = str.split("\\(");
        return Arrays.binarySearch(Constants.ALLOWED_MATH_FUNCTIONS, funcArray[0].toLowerCase()) >= 0;
    }

    private static boolean isTokenValid(String token) {
        return isTokenExit(token) || isTokenNumerical(token) || isTokenMathFunction(token) || isTokenDelimiter(token);
    }

    private static boolean isTokenExit(String token) {
        return token.toLowerCase().equals(Constants.COMMAND_EXIT);
    }

    private static String filterToken(String token) {
        return token.trim();
    }
    
    private static boolean isTokenDelimiter(String token) {
        return Constants.DELIMITERS.indexOf(token) >= 0;
    }

}
