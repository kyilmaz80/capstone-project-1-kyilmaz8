import java.util.Arrays;
import java.util.Stack;
import java.util.StringTokenizer;

public class CommandParser {
    public static String parse(String command) {
        return convertToPostfixExpression(command);
    }

    public static double execute(String command) {
        //TODO: komutun parse edilmis postfix halinin hesabi
        String postfixExpression = command;
        double result = 0;
        // read the expression from left to right
        // push the element in to a stack if it is operand
        // if the current character is an operator,
        //   pop the two operands from the stack and
        //   evaluate it
        //   push back the result of the evaluation
        // repeat until the end of the expression
        Stack<Double> stack = new Stack<Double>();
        String tokenString;
        for(char tokenChar: postfixExpression.toCharArray()) {
            tokenString = "" + tokenChar;
            if (isTokenOperand(tokenString)) {
                stack.push(Double.valueOf(tokenString));
            } else {
                // token is operator
                Double val1 = stack.pop();
                Double val2 = stack.pop();
                result = doOperation(val1, val2, Operators.fromSymbol(tokenString));
                stack.push(result);
            }
        }
        return stack.pop();
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
                    //push back
                    stack.push(opOnStack);
                    Operators opOnStackOperator = Operators.fromSymbol(opOnStack);
                    Operators tokenOperator = Operators.fromSymbol(tokenString);
                    if (opOnStackOperator.isOperatorHighestPriorityFrom(tokenOperator) ||
                        opOnStackOperator.isOperatorSamePriorityTo(tokenOperator)) {
                        //rule: highest priority must be on top!
                        //we have to pop from stack to postfix expression
                        //then push it
                        //since we already popped it
                        //rule: no same priority operators stay together
                        sb.append(stack.pop());
                        stack.push(tokenString);
                    } else {
                        stack.push(tokenString);
                    }
                }
            }
        }
        // add the remaining operators to postfix expression
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }

        return sb.toString();
    }
    private static double doOperation(double val1, double val2, Operators op) {
        double result = -1;
        switch(op) {
            case MULTIPLICATION -> result = val2 * val1;
            case DIVISION -> result = val2 / val1;
            case ADDITION -> result = val2 + val1;
            case SUBTRACTION -> result = val2 - val1;
            default -> System.out.println("NOT IMPLEMENTED!");
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
