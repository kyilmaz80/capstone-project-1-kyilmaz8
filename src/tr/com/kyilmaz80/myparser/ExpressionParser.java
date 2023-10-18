package tr.com.kyilmaz80.myparser;

import tr.com.kyilmaz80.myparser.func.*;
import tr.com.kyilmaz80.myparser.utils.Operators;
import tr.com.kyilmaz80.myparser.utils.Constants;
import tr.com.kyilmaz80.myparser.utils.StackUtils;
import tr.com.kyilmaz80.myparser.utils.StringUtils;
import tr.com.kyilmaz80.myparser.utils.TokenUtils;

import java.util.Stack;
import java.util.StringTokenizer;

public class ExpressionParser {
    public static FunctionCalculator fc = FunctionCalculatorFactory.getInstance();
    public static String parse(String command) {
        return convertToPostfixExpression(StringUtils.removeSpaces(command));
    }


    private static String convertToPostfixExpression(String infixExpression) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(infixExpression, Constants.DELIMITERS, true);
        Stack<String> stack = new Stack<>();
        String funcName = "";
        String funcName2 = "";
        boolean leftParanthesisFound = false;
        boolean funcFound = false;
        int funcCommaCount = 0;
        int funcArgCount;

        // variadic multiarg postfix expression support
        // if func found flag it
        // if left parenthesis found
        //  flag it
        //  let count of comma count to zero
        // if func and left paranthesis flag in comma case
        //  increment comma count
        // if right parenthesis found
        //  if func and left paranthesis flag
        //   let arg count to comma count plus 1
        //   clear func and left paranthesis flag
        //   convert sb to str, split with whitespace,
        //   get the length of array - (arg_count + 1) th item which is func
        //   found the func before arg count and concat arg count to func

        while (st.hasMoreElements()) {
            String tokenString = TokenUtils.filterToken(st.nextToken());
            if (!TokenUtils.isTokenValid(tokenString)) {
                System.err.println(tokenString + " " + Bundle.get().getString("cp_err5"));
                return null;
            }
            if (TokenUtils.isTokenOperand(tokenString)) {
                //if (TokenUtils.isTokenMathFunction(tokenString) && TokenUtils.isMathFunctionVariadic(tokenString)) {
                if (TokenUtils.isTokenMathFunction(tokenString) && TokenUtils.isMathFunctionVariadic(tokenString)) {
                    funcFound = true;
                    funcCommaCount = 0;
                    funcName = tokenString;
                } else if(TokenUtils.isTokenMathFunction(tokenString)) {
                    funcName2 = tokenString;
                }
                sb.append(tokenString.concat(Constants.WHITESPACE));
            } else {
                // token is operator
                Operators tokenOperator = Operators.fromSymbol(tokenString);
                if (tokenOperator == Operators.LEFT_PARENTHESIS) {
                    leftParanthesisFound = funcFound == true;
                }
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

                    if (tokenOperator == Operators.LEFT_PARENTHESIS) {
                        if (!TokenUtils.isTokenMathFunction(opOnStack)) {
                            //LEFT PARENTHESES prior operation case
                            stack.push(tokenString);
                            continue;
                        }
                    } else if (tokenOperator == Operators.RIGHT_PARENTHESIS) {
                        Operators topOperator = Operators.fromSymbol(stack.peek());

                        if (StackUtils.isBeforeLastOnStackIsLeftParentheses(stack) && !StackUtils.isBeforeLastOnStackIsFunction(stack)) {
                            //sb.append(stack.pop().concat(tr.com.kyilmaz80.myparser.utils.Constants.WHITESPACE));
                            if (topOperator != Operators.LEFT_PARENTHESIS) {
                                StringUtils.doAppendOperatorToPostfixExpression(sb, stack.pop().concat(Constants.WHITESPACE));
                            }
                            stack.pop();
                            continue;
                        }
                        //while the operator at the top of the operator stack is not a left parenthesis:
                        while (topOperator != Operators.LEFT_PARENTHESIS) {
                            if (stack.isEmpty()) {
                                //return null;
                                System.err.println(Bundle.get().getString("cp_err6"));
                                break;
                            }
                            //pop the operator from the operator stack into the output queue (sb)
                            StringUtils.doAppendOperatorToPostfixExpression(sb, stack.pop().concat(Constants.WHITESPACE));
                            if (stack.isEmpty()) {
                                System.err.println(Bundle.get().getString("cp_err7"));
                                return null;
                            }
                            topOperator = Operators.fromSymbol(stack.peek());
                        }
                        //pop the left parenthesis from the operator stack and discard it
                        if (topOperator == Operators.LEFT_PARENTHESIS) {
                            stack.pop();
                        }

                        // replace the func name with argCount for variadic if found
                        if (funcFound && leftParanthesisFound) {
                            funcFound = false;
                            leftParanthesisFound = false;
                            funcArgCount = ++funcCommaCount;
                            // change only multi arg variadic func names
                            // not pow(x,y) like two or sqrt(x)
                            MathFunction mathFunction = fc.getFunction(funcName);
                            if (mathFunction instanceof MultiArgMathFunction) {
                                StringUtils.doReplaceToPostfixExpression(sb, funcArgCount);
                            }
                        }

                        continue;
                    } else if (tokenOperator == Operators.FUNC_VARIABLE_COMMA) {
                        //System.out.println("Comma var");
                        if (!funcName2.isEmpty()) {
                            MathFunction mathFunction = fc.getFunction(funcName2);
                            if (mathFunction instanceof DoubleArgMathFunction) {
                                funcCommaCount--;
                            } else if (mathFunction instanceof  MultiArgMathFunction) {
                                throw new RuntimeException(Bundle.get().getString("cp_err8"));
                            }
                        }
                        funcCommaCount++;
                        continue;
                    }

                    if (opOnStackOperator == null || tokenOperator == null) {
                        throw new RuntimeException(Bundle.get().getString("cp_err9"));
                    }
                    if (opOnStackOperator.isOperatorHighestPriorityFrom(tokenOperator) ||
                            opOnStackOperator.isOperatorSamePriorityTo(tokenOperator) ||
                            opOnStackOperator == tokenOperator) {
                        //rule: highest priority must be on top!
                        //we have to pop from stack to postfix expression
                        //then push it
                        //since we already popped it
                        //rule: no same priority operators stay together
                        String swappedTopOperator = stack.pop();
                        //sb.append(swappedTopOperator.concat(tr.com.kyilmaz80.myparser.utils.Constants.WHITESPACE));
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
            //sb.append(stack.pop().concat(tr.com.kyilmaz80.myparser.utils.Constants.WHITESPACE));
        }

        if (StringUtils.isStringContainsParentheses(sb.toString())) {
            System.err.println(Bundle.get().getString("cp_err10"));
            return null;
        }
        return sb.toString().trim();
    }
}
