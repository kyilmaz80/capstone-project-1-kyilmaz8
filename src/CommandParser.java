import java.util.Stack;
import java.util.StringTokenizer;

public class CommandParser {
    public static String parse(String command) {
        return convertToPostfixExpression(StringUtils.removeSpaces(command));
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
            tokenString = TokenUtils.filterToken(st.nextToken());
            if (tokenString.isEmpty()) {
                continue;
            }
            if (TokenUtils.isTokenMathFunction(tokenString)) {
                stack.push(tokenString);
            } else if (TokenUtils.isTokenOperand(tokenString)) {
                if(isBeforeLastOnStackIsFunction(stack)) {
                    String funcStr = doGetBeforeLastOnStack(stack);
                    if (getFunctionArgCount(funcStr) == 2) {
                        stack.push(tokenString);
                        result = doFuncOperationOnStack(stack, funcStr);
                        stack.pop();
                        stack.push(String.valueOf(result));
                        continue;
                    }
                }
                if(!stack.isEmpty() && TokenUtils.isTokenMathFunction(stack.peek())) {
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
                    if (TokenUtils.isTokenMathFunction(stack.peek())) {
                        stack.push(tokenString);
                    }
                    result = doFuncOperationOnStack(stack, funcStr);
                    stack.pop();
                    stack.push(String.valueOf(result));
                }else {
                    stack.push(tokenString);
                }

            } else {
                // token is operator
                // if the two elements on stack are operands
                String funcStr = doGetFuncNameOnStack(stack);
                String topElementNext = "";

                //no func before operator!
                //pow case
                if (TokenUtils.isTokenMathFunction(funcStr) && getFunctionArgCount(funcStr) == 2) {
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
                if (TokenUtils.isTokenMathFunction(topElementNext)) {
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
            String tokenString = TokenUtils.filterToken(st.nextToken());
            if (!TokenUtils.isTokenValid(tokenString)) {
                System.err.println(tokenString + " token i beklenmedik!");
                return null;
            }
            if (TokenUtils.isTokenOperand(tokenString)) {
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

                    if (tokenOperator == Operators.LEFT_PARENTHESES) {
                        if (!TokenUtils.isTokenMathFunction(opOnStack)) {
                            //LEFT PARENTHESES prior operation case
                            stack.push(tokenString);
                            continue;
                        }
                    } else if (tokenOperator == Operators.RIGHT_PARENTHESES) {
                        Operators topOperator = Operators.fromSymbol(stack.peek());

                        if (isBeforeLastOnStackIsLeftParentheses(stack) && !isBeforeLastOnStackIsFunction(stack)) {
                            //sb.append(stack.pop().concat(Constants.WHITESPACE));
                            doAppendOperatorToPostfixExpression(sb, stack.pop().concat(Constants.WHITESPACE));
                            stack.pop();
                            continue;
                        }
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

                    /*if ((opOnStackOperator.isOperatorHighestPriorityFrom(tokenOperator) ||
                        opOnStackOperator.isOperatorSamePriorityTo(tokenOperator)) &&
                            opOnStackOperator != tokenOperator) {
                     */

                    if (opOnStackOperator.isOperatorHighestPriorityFrom(tokenOperator) ||
                         opOnStackOperator.isOperatorSamePriorityTo(tokenOperator) ||
                         opOnStackOperator == tokenOperator) {
                        //rule: highest priority must be on top!
                        //we have to pop from stack to postfix expression
                        //then push it
                        //since we already popped it
                        //rule: no same priority operators stay together
                        String swappedTopOperator = stack.pop();
                        //sb.append(swappedTopOperator.concat(Constants.WHITESPACE));
                        doAppendOperatorToPostfixExpression(sb, swappedTopOperator.concat(Constants.WHITESPACE));
                        //stack.push(swappedTopOperator);
                    }
                    stack.push(tokenString);
                }
            }
        }
        // add the remaining operators to postfix expression
        while (!stack.isEmpty()) {
            doAppendOperatorToPostfixExpression(sb, stack.pop().concat(Constants.WHITESPACE));
            //sb.append(stack.pop().concat(Constants.WHITESPACE));
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

        if (TokenUtils.isTokenMathFunction(topElement)) {
            return topElement;
        } else if (TokenUtils.isTokenMathFunction(topElementNext)) {
            return topElementNext;
        } else if (TokenUtils.isTokenMathFunction(topElementNext2)) {
            return topElementNext2;
        } else {
            //System.err.println("Stack overflow for function search!");
            return null;
        }
    }

    private static void doAppendOperatorToPostfixExpression(StringBuilder sb, String element) {
        String str = sb.toString().trim();
        int lastIndex = str.length()-1;
        String last = String.valueOf(str.charAt(lastIndex));
        Operators opNew = Operators.fromSymbol(element.trim());
        Operators opLast = Operators.fromSymbol(last);

        if (opNew == Operators.LEFT_PARENTHESES || opNew == Operators.RIGHT_PARENTHESES) {
            return;
        }

        if (opNew == null || opLast == null) {
            sb.append(element);
            return;
        }
        // dirty poor man's cozum
        //right parentheses not expected
        //because of poor code when input is function

        if (opNew.isOperatorHighestPriorityFrom(opLast)) {
            sb.append(element);
        }else if (opNew.isOperatorSamePriorityTo(opLast)) {
            System.err.println("Postfix operator eklerken operator ayni geldi!");
        } else {
            sb.append(element);
        }
    }
    private static boolean isLastTwoFuncOnStack(Stack<String> stack) {
        if (stack.size() < 2) {
            return false;
        }
        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);

        return TokenUtils.isTokenMathFunction(op1) && TokenUtils.isTokenMathFunction(op2);
    }

    private static boolean isBeforeLastOnStackIsFunction(Stack<String> stack) {
        if (stack.size() < 2) {
            return false;
        }
        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);

        return TokenUtils.isTokenMathFunction(op2);
    }

    private static String doGetBeforeLastOnStack(Stack<String> stack) {
        if (stack.size() < 2) {
            return null;
        }
        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);
        return op2;
    }
    private static boolean isBeforeLastOnStackIsLeftParentheses(Stack<String> stack) {
        if (stack.size() < 2) {
            return false;
        }
        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);

        return Operators.fromSymbol(op2) == Operators.LEFT_PARENTHESES;
    }

    private static boolean isOperationOnStackArithmetic(Stack<String> stack) {
        String val1 = stack.pop();
        String val2 = stack.pop();
        stack.push(val2);
        stack.push(val1);

        return TokenUtils.isTokenNumerical(val1) && TokenUtils.isTokenNumerical(val2);
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
            case "cos","sin","sqrt" -> result = 1;
            case "pow" -> result = 2;
            default -> System.out.println("NOT IMPLEMENTED!");
        }
        return result;
    }
}
