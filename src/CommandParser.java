import java.util.Arrays;
import java.util.Stack;
import java.util.StringTokenizer;

public class CommandParser {
    public static String parse(String command) {
        return convertToPostfixExpression(command);
    }

    public static double execute(String postfixExpression) {
        double result;
        // read the expression from left to right
        // push the element in to a stack if it is operand
        // if the current character is an operator,
        //   pop the two operands from the stack and
        //   evaluate it
        //   push back the result of the evaluation
        // repeat until the end of the expression
        Stack<String> stack = new Stack<>();
        String tokenString;
        Operators operator;
        StringTokenizer st = new StringTokenizer(postfixExpression, Constants.DELIMITERS, true);
        //for(char tokenChar: postfixExpression.toCharArray()) {
        while(st.hasMoreElements()) {
            tokenString = filterToken(st.nextToken());
            if (tokenString.isEmpty()) {
                continue;
            }
            if (isTokenMathFunction(tokenString)) {
                stack.push(tokenString);
            } else if (isTokenOperand(tokenString)) {
                if(!stack.isEmpty() && isTokenMathFunction(stack.peek())) {
                    //single valued variable icin
                    String funcStr = stack.pop();
                    double val = Double.parseDouble(tokenString);
                    result = doCalculateFunction(funcStr, new double[]{val});
                    stack.push(String.valueOf(result));
                }else {
                    stack.push(tokenString);
                }

            } else {
                // token is operator
                operator = Operators.fromSymbol(tokenString);
                double val1 = Double.parseDouble(stack.pop());
                if (isTokenMathFunction(stack.peek())) {
                    String funcStr = stack.pop();
                    double val = val1;
                    result = doCalculateFunction(funcStr, new double[]{val});
                    stack.push(String.valueOf(result));
                    //TODO: multi-valued func?
                    val1 = Double.parseDouble(stack.pop());
                }

                double val2 = Double.parseDouble(stack.pop());
                if (operator == null) {
                    System.err.println("Operator null geldi!");
                    System.exit(1);
                }
                result = doOperation(val1, val2, operator);
                stack.push(String.valueOf(result));
            }
        }
        return Double.parseDouble(stack.pop());
    }

    private static String convertToPostfixExpression(String infixExpression) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(infixExpression, Constants.DELIMITERS, true);
        Stack<String> stack = new Stack<>();
        while(st.hasMoreElements()) {
            String tokenString = filterToken(st.nextToken());
            if (!isTokenValid(tokenString)) {
                System.err.println(tokenString + " token i beklenmedik!");
                return null;
            }
            if (isTokenOperand(tokenString)) {
                if (tokenString.equalsIgnoreCase("(")) {
                    System.out.println("csdcd");
                }
                sb.append(tokenString.concat(Constants.WHITESPACE));
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

                    String opOnStack = stack.peek();
                    Operators opOnStackOperator = Operators.fromSymbol(opOnStack);
                    Operators tokenOperator = Operators.fromSymbol(tokenString);
                    if (tokenOperator == Operators.RIGHT_PARENTHESES) {
                        Operators topOperator = Operators.fromSymbol(stack.peek());
                        while(topOperator == Operators.LEFT_PARENTHESES ) {
                            if (stack.isEmpty()) {
                                System.err.println("Mismatched parentheses problem!");
                                return null;
                            }
                            stack.pop();
                            topOperator = Operators.fromSymbol(stack.peek());
                        }
                        continue;
                    }

                    if ((opOnStackOperator.isOperatorHighestPriorityFrom(tokenOperator) ||
                        opOnStackOperator.isOperatorSamePriorityTo(tokenOperator)) && opOnStackOperator != tokenOperator) {

                        //rule: highest priority must be on top!
                        //we have to pop from stack to postfix expression
                        //then push it
                        //since we already popped it
                        //rule: no same priority operators stay together

                        sb.append(stack.pop().concat(Constants.WHITESPACE));
                        stack.push(tokenString);
                    } else {
                        stack.push(tokenString);
                    }
                }
            }
        }
        // add the remaining operators to postfix expression
        while (!stack.isEmpty()) {
            sb.append(stack.pop().concat(Constants.WHITESPACE));
        }

        return sb.toString().trim();
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

    private static double doCalculateFunction(String funcStr, double[] args) {
        double result = -1;
        //String[] funcArray = funcStr.split("\\(");
        //String func = funcArray[0].toLowerCase();
        //double val = double.valueOf(funcs);
        switch(funcStr) {
            case "cos" -> result = Math.cos(args[0]);
            case "sin" -> result = Math.sin(args[0]);
            case "pow" -> result = Math.pow(args[1], args[0]);
            case "sqrt" -> result = Math.sqrt(args[0]);
            default -> System.out.println("NOT IMPLEMENTED!");
        }
        return result;
        //return Arrays.binarySearch(Constants.ALLOWED_MATH_FUNCTIONS, funcArray[0].toLowerCase()) >= 0;
    }

    private static int getFunctionArgCount(String funcStr) {
        int result = 0;
        switch(funcStr) {
            case "cos" -> result = 1;
            case "sin" -> result = 1;
            case "pow" -> result = 2;
            case "sqrt" -> result = 1;
            default -> System.out.println("NOT IMPLEMENTED!");
        }
        return result;
    }

    private static boolean isTokenOperand(String str) {
        return isTokenNumerical(str) || isTokenMathFunction(str);
    }

    private static boolean isTokenNumerical(String str) {
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

    private static boolean isTokenMathFunction(String str) {
        if (str.length() == 1) {
            return false;
        }
        String[] funcArray = str.split("\\(");
        return Arrays.binarySearch(Constants.ALLOWED_MATH_FUNCTIONS, funcArray[0].toLowerCase()) >= 0;
    }

    private static boolean isTokenValid(String token) {
        return isTokenExit(token) || isTokenNumerical(token) || isTokenMathFunction(token) || isTokenDelimiter(token);
    }

    private static boolean isTokenExit(String token) {
        return token.equalsIgnoreCase(Constants.COMMAND_EXIT);
    }

    private static String filterToken(String token) {
        return token.trim();
    }
    
    private static boolean isTokenDelimiter(String token) {
        return Constants.DELIMITERS.contains(token);
    }
}
