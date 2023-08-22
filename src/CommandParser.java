import java.util.Arrays;
import java.util.Stack;
import java.util.StringTokenizer;

public class CommandParser {
    public static String parse(String command) {
        return convertToPostfixExpression(command);
    }

    public static double eval(String postfixExpression) {
        double result;
        // read the expression from left to right
        // push the element in to a stack if it is operand
        // if the current character is an operator,
        //   pop the two operands from the stack and
        //   evaluate it
        //   push back the result of the evaluation
        // repeat until the end of the expression
        Stack<String> stack = new Stack<>();
        String tokenString = null;
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
                    String funcStr = stack.peek();
                    if(getFunctionArgCount(funcStr) == 2) {
                        stack.push(tokenString);
                        continue;
                    }

                    if (isLastTwoFuncOnStack(stack)) {
                        //nested func case
                        stack.push(tokenString);
                    }
                    //push back val for func
                    if (isTokenMathFunction(stack.peek())) {
                        stack.push(tokenString);
                    }
                    result = doFuncOperationOnStack(stack, funcStr);
                    stack.pop();
                    stack.push(String.valueOf(result));

                    /*
                    String funcStr = stack.pop();
                    double val = Double.parseDouble(tokenString);
                    result = doCalculateFunction(funcStr, new double[]{val});
                    stack.push(String.valueOf(result));

                     */
                }else {
                    stack.push(tokenString);
                }

            } else {
                // token is operator
                // if the two elements on stack are operands
                /*
                String topElement = stack.pop();
                String topElementNext = stack.pop();
                String topElementNext2 = "";
                if (!stack.isEmpty() || stack.size() > 2) {
                    topElementNext2 = stack.pop();
                    stack.push(topElementNext2);
                }
                stack.push(topElementNext);
                stack.push(topElement);
                 */
                String funcStr = doGetFuncNameOnStack(stack);
                String topElementNext = "";

                //no func before operator!
                //pow case
                if (isTokenMathFunction(funcStr) && getFunctionArgCount(funcStr) == 2) {
                    //double valued func case
                    //System.err.println("NOT IMPLEMENTED");
                    //call by ref!remember! stack!
                    result = doFuncOperationOnStack(stack, funcStr);
                    //pop the func
                    stack.pop();
                    //push result
                    stack.push(String.valueOf(result));
                    topElementNext = stack.peek();
                }
                //may be func before before operator
                if (isTokenMathFunction(topElementNext)) {
                    /*
                    varCount = getFunctionArgCount(topElementNext);
                    double[] vals = new double[varCount];
                    for(int i = 0; i < varCount; i++) {
                        vals[i] = Double.valueOf(stack.pop());
                    }
                    result = doCalculateFunction(topElementNext, vals);
                     */
                    result = doFuncOperationOnStack(stack, topElementNext);
                    //disregard the func
                    stack.pop();
                    stack.push(String.valueOf(result));
                } else {
                    if (isOperationOnStackFunc(stack)) {
                        funcStr = doGetFuncNameOnStack(stack);
                        result = doFuncOperationOnStack(stack, funcStr);
                        //pop the func
                        stack.pop();
                    } else {
                        result = doArithmeticOperationOnStack(stack, tokenString);
                    }
                    stack.push(String.valueOf(result));
                }
                /*
                operator = Operators.fromSymbol(tokenString);
                String topElement = stack.pop();
                double val1, val2;

                if (isTokenMathFunction(stack.peek())) {
                    String funcStr = stack.pop();
                    double val = Double.parseDouble(topElement);
                    result = doCalculateFunction(funcStr, new double[]{val});
                    stack.push(String.valueOf(result));
                    //TODO: multi-valued func?
                    val1 = Double.parseDouble(stack.pop());
                }
                val1 = Double.parseDouble(topElement);
                val2 = Double.parseDouble(stack.pop());
                if (operator == null) {
                    System.err.println("Operator null geldi!");
                    System.exit(1);
                }
                result = doOperation(val1, val2, operator);
                stack.push(String.valueOf(result));

                 */
            }
        }
        while(stack.size() != 1) {
            if (isOperationOnStackArithmetic(stack)) {
                result = doArithmeticOperationOnStack(stack, tokenString);
            } else {
                result = doFuncOperationOnStack(stack, tokenString);
            }
            stack.push(String.valueOf(result));
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
                    } else if(tokenOperator == Operators.FUNC_VARIABLE_COMMA) {
                        //System.out.println("Comma var");
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
            default -> System.out.println("ARITHMETIC OP NOT IMPLEMENTED!");
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
            default -> System.out.println("FUNC NOT IMPLEMENTED!");
        }
        return result;
        //return Arrays.binarySearch(Constants.ALLOWED_MATH_FUNCTIONS, funcArray[0].toLowerCase()) >= 0;
    }

    private static double doArithmeticOperationOnStack(Stack<String> stack, String tokenString) {
        double val1 = Double.parseDouble(stack.pop());
        //can be operand or func
        double val2 = Double.parseDouble(stack.pop());

        Operators operator = Operators.fromSymbol(tokenString);
        return doOperation(val1, val2, operator);
        //stack.push(String.valueOf(result));
    }

    private static double doFuncOperationOnStack(Stack<String> stack, String funcStr) {

        int varCount = getFunctionArgCount(funcStr);
        double[] vals = new double[varCount];
        for(int i = 0; i < varCount; i++) {
            vals[i] = Double.parseDouble(stack.pop());
        }
        //disregard the func
        //stack.pop();
        //stack.push(String.valueOf(result));
        return doCalculateFunction(funcStr, vals);
    }

    private static String doGetFuncNameOnStack(Stack<String> stack) {
        String topElement = stack.pop();
        String topElementNext = stack.pop();
        String topElementNext2 = "";
        if (!stack.isEmpty() || stack.size() > 2) {
            topElementNext2 = stack.pop();
            stack.push(topElementNext2);
        }
        stack.push(topElementNext);
        stack.push(topElement);

        if (isTokenMathFunction(topElement)) {
            return topElement;
        } else if (isTokenMathFunction(topElementNext)) {
            return topElementNext;
        } else if (isTokenMathFunction(topElementNext2)) {
            return topElementNext2;
        } else {
            //System.err.println("Stack overflow for function search!");
            return null;
        }
    }

    private static boolean isLastTwoFuncOnStack(Stack<String> stack) {
        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);

        return isTokenMathFunction(op1) && isTokenMathFunction(op2);
    }
    private static boolean isOperationOnStackArithmetic(Stack<String> stack) {
        String val1 = stack.pop();
        String val2 = stack.pop();
        stack.push(val2);
        stack.push(val1);

        return isTokenNumerical(val1) && isTokenNumerical(val2);
    }

    private static boolean isOperationOnStackFunc(Stack<String> stack) {
        return !isOperationOnStackArithmetic(stack);
    }

    private static int getFunctionArgCount(String funcStr) {
        int result = 0;
        if (funcStr.equalsIgnoreCase("") || funcStr == null) {
            return -1;
        }
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
        if (str == null || str.length() <= 1) {
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
