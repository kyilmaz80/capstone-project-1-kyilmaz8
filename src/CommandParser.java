import java.util.Stack;
import java.util.StringTokenizer;

public class CommandParser {
    public static String parse(String command) {
        String postfixExpression = convertToPostfixExpression(StringUtils.removeSpaces(command));
        return postfixExpression;
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
        if (postfixExpression == null) {
            System.err.println("Beklenmeyen postfix ifadesi null!");
            System.exit(1);
        }
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
                if(StackUtils.isBeforeLastOnStackIsFunction(stack)) {
                    String funcStr = StackUtils.doGetBeforeLastOnStack(stack);
                    if (StackUtils.getFunctionArgCount(funcStr) == 2) {
                        stack.push(tokenString);
                        result = StackUtils.doFuncOperationOnStack(stack, funcStr);
                        stack.pop();
                        stack.push(String.valueOf(result));
                        continue;
                    }
                }
                if(!stack.isEmpty() && TokenUtils.isTokenMathFunction(stack.peek())) {
                    //single valued variable icin
                    String funcStr = stack.peek();
                    if(StackUtils.getFunctionArgCount(funcStr) == 2) {
                        stack.push(tokenString);
                        continue;
                    }

                    if (StackUtils.isLastTwoFuncOnStack(stack)) {
                        //nested func case
                        stack.push(tokenString);
                    }
                    //push back val for func
                    if (TokenUtils.isTokenMathFunction(stack.peek())) {
                        stack.push(tokenString);
                    }
                    result = StackUtils.doFuncOperationOnStack(stack, funcStr);
                    stack.pop();
                    stack.push(String.valueOf(result));
                }else {
                    stack.push(tokenString);
                }

            } else {
                // token is operator
                // if the two elements on stack are operands
                //String funcStr = StackUtils.doGetFuncNameOnStack(stack);
                //String topElementBefore = StackUtils.doGetBeforeLastOnStack(stack);
                String funcStr = StackUtils.doGetBeforeLastOnStack(stack);
                String topElementNext;

                //no func before operator!
                //pow case
                if (TokenUtils.isTokenMathFunction(funcStr) && StackUtils.getFunctionArgCount(funcStr) == 2) {
                    //double valued func case
                    //System.err.println("NOT IMPLEMENTED");
                    //call by ref!remember! stack!
                    result = StackUtils.doFuncOperationOnStack(stack, funcStr);
                    //pop the func
                    stack.pop();
                    //push result
                    stack.push(String.valueOf(result));
                    topElementNext = stack.peek();
                }
                //topElementNext = stack.peek();
                //may be func before before operator
                else if (TokenUtils.isTokenMathFunction(funcStr)) {
                    result = StackUtils.doFuncOperationOnStack(stack, funcStr);
                    //disregard the func
                    stack.pop();
                    stack.push(String.valueOf(result));
                    //is remanining operator operation left?
                    if (TokenUtils.isTokenArithmeticOperator(tokenString)) {
                        result = StackUtils.doArithmeticOperationOnStack(stack, tokenString);
                        stack.push(String.valueOf(result));
                    }
                } else {
                    if (StackUtils.isOperationOnStackFunc(stack)) {
                        funcStr = StackUtils.doGetBeforeLastOnStack(stack);
                        result = StackUtils.doFuncOperationOnStack(stack, funcStr);
                        //pop the func
                        stack.pop();
                    } else {
                        result = StackUtils.doArithmeticOperationOnStack(stack, tokenString);
                    }
                    stack.push(String.valueOf(result));
                }
            }
        }
        while(stack.size() != 1) {
            if (StackUtils.isOperationOnStackArithmetic(stack)) {
                result = StackUtils.doArithmeticOperationOnStack(stack, tokenString);
            } else {
                result = StackUtils.doFuncOperationOnStack(stack, tokenString);
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

                        if (StackUtils.isBeforeLastOnStackIsLeftParentheses(stack) && !StackUtils.isBeforeLastOnStackIsFunction(stack)) {
                            //sb.append(stack.pop().concat(Constants.WHITESPACE));
                            if (topOperator != Operators.LEFT_PARENTHESES) {
                                StringUtils.doAppendOperatorToPostfixExpression(sb, stack.pop().concat(Constants.WHITESPACE));
                            }
                            stack.pop();
                            continue;
                        }
                        //while the operator at the top of the operator stack is not a left parenthesis:
                        while(topOperator != Operators.LEFT_PARENTHESES ) {
                            if (stack.isEmpty()) {
                                //return null;
                                System.err.println("Stack empty @RIGHT PARENTHESIS");
                                break;
                                //topOperator = Operators.fromSymbol(stack.peek());

                            }
                            //pop the operator from the operator stack into the output queue (sb)
                            StringUtils.doAppendOperatorToPostfixExpression(sb, stack.pop().concat(Constants.WHITESPACE));
                            topOperator = Operators.fromSymbol(stack.peek());
                        }
                        //pop the left parenthesis from the operator stack and discard it
                        if (topOperator == Operators.LEFT_PARENTHESES) {
                            stack.pop();
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
                        StringUtils.doAppendOperatorToPostfixExpression(sb, swappedTopOperator.concat(Constants.WHITESPACE));
                        //stack.push(swappedTopOperator);
                    }
                    stack.push(tokenString);
                }
            }
        }
        // add the remaining operators to postfix expression
        // while there are tokens on the operator stack:
        while (!stack.isEmpty()) {
            StringUtils.doAppendOperatorToPostfixExpression(sb, stack.pop().concat(Constants.WHITESPACE));
            //sb.append(stack.pop().concat(Constants.WHITESPACE));
        }

        if (StringUtils.isStringContainsParentheses(sb.toString())) {
            System.err.println("Mismatched parentheses problem!");
            //return null;
        }

        return sb.toString().trim();
    }




}
