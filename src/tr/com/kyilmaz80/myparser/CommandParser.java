package tr.com.kyilmaz80.myparser;

import tr.com.kyilmaz80.myparser.func.*;
import tr.com.kyilmaz80.myparser.utils.Operators;
import tr.com.kyilmaz80.myparser.utils.Constants;
import tr.com.kyilmaz80.myparser.utils.StackUtils;
import tr.com.kyilmaz80.myparser.utils.StringUtils;
import tr.com.kyilmaz80.myparser.utils.TokenUtils;

import javax.swing.*;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TooManyListenersException;

public class CommandParser {
    public static FunctionCalculator fc = FunctionCalculatorFactory.getInstance();
    public static String parse(String command) {
        return convertToPostfixExpression(StringUtils.removeSpaces(command));
    }


    public static Double eval(String postfixExpression) {
        Double result;
        Stack<String> stack = new Stack<>();
        Stack<String> opStack = new Stack<>();
        String tokenString;
        if (postfixExpression == null) {
            System.err.println("Beklenmeyen postfix ifadesi null!");
            System.exit(1);
        }
        StringTokenizer st = new StringTokenizer(postfixExpression, Constants.DELIMITERS, true);
        while (st.hasMoreElements()) {
            tokenString = TokenUtils.filterToken(st.nextToken());
            if (tokenString.isEmpty()) {
                continue;
            }
            if (TokenUtils.isTokenNumerical(tokenString)) {
                // if stack is not empty and stack peek is single valued func
                //   pop the func and calculate func
                //   push the result to stack
                // else if stack is not empty and stack peek is double valued func
                //   get the nextToken, pop the func and calculate with args tokenString,nextToken
                //   push the result to stack
                // else if stack is not empty and stack peek is multi arg variadic func
                //   get the func args x count then get the next x tokens
                //   calculate the variadic func with these args
                //   push the result to stack
                if (stack.isEmpty() || TokenUtils.isTokenNumerical(stack.peek())) {
                    stack.push(tokenString);
                }else {
                    // stack is not empty and stack peek is not numerical case
                    // tokenString parent may be single, double or multi arg func
                    MathFunction mf = fc.getFunction(stack.peek());
                    if (mf == null) {
                        System.err.println("not a func mf null");
                        continue;
                    }

                    if (TokenUtils.isTokenNumerical(tokenString)) {
                        stack.push(tokenString);
                    }

                    if (!StackUtils.doCalculateFuncOnStack(stack, mf, st)) {
                        throw new RuntimeException("Func operation problem on stack!");
                    }
                    /*
                    Double calcVal;
                    if (mf instanceof SingleArgMathFunction samf) {
                        double val = Double.parseDouble(tokenString);
                        calcVal = samf.calculate(val);
                        stack.push(calcVal.toString());
                    }else if (mf instanceof DoubleArgMathFunction damf) {
                        double val = Double.parseDouble(tokenString);
                        st.nextToken(); //ignore whitespace
                        double nextVal = Double.parseDouble(TokenUtils.filterToken(st.nextToken()));
                        calcVal = damf.calculate(val, nextVal);
                        stack.push(calcVal.toString());
                    }else if (mf instanceof MultiArgMathFunction mamf) {
                        int count = mamf.getArgCount();
                        Double[] vals = new Double[count];
                        for (int i = 0; i < count; i++) {
                            st.nextToken();  //ignore whitespace
                            vals[i] = Double.parseDouble(TokenUtils.filterToken(st.nextToken()));
                        }
                        calcVal = mamf.calculate(vals);
                        stack.push(calcVal.toString());
                    }else {
                        System.err.println("NOT IMPLEMENTED FUNC TYPE!");
                    }

                     */

                }
            } else if(TokenUtils.isTokenMathFunction(tokenString)) {
                stack.push(tokenString);
            }else if (TokenUtils.isTokenArithmeticOperator(tokenString)) {
                //pop the top two operands
                //if the two operands are numerical
                // do arithmetical operation
                opStack.push(tokenString);
                if (StackUtils.isLastTwoNumOnStack(stack)) {
                    /*
                    result = StackUtils.doArithmeticOperationOnStack(stack, tokenString);
                    stack.push(result.toString());
                    opStack.pop();
                     */
                    if (!StackUtils.doCalculateArithmeticOnStack(stack, tokenString)){
                        throw new RuntimeException("Arithmetic operation problem on stack!");
                    }
                    opStack.pop();
                } else {
                    // before peek is a function
                    String funcName = StackUtils.getBeforeLastFunctionOnStack(stack);
                    MathFunction mf = fc.getFunction(funcName);

                    if (StackUtils.isBeforeLastOnStackIsFunction(stack)) {
                        System.out.println("before a func!");
                        if (!StackUtils.doCalculateFuncOnStack(stack, mf, st)) {
                            throw new RuntimeException("Func operation problem on stack!");
                        }
                    }
                    //remaining arithmetic
                    if (opStack.size() != 0) {
                        if (StackUtils.isLastTwoNumOnStack(stack)) {
                            /*
                            result = StackUtils.doArithmeticOperationOnStack(stack, opStack.pop());
                            stack.push(result.toString());
                            //opStack.pop();
                             */
                            if (!StackUtils.doCalculateArithmeticOnStack(stack, opStack.pop())){
                                throw new RuntimeException("Arithmetic operation problem on stack!");
                            }
                        }
                    }
                }

            }
        }
       // System.out.println(stack.size());
        return Double.parseDouble(stack.pop());
    }

    private static String convertToPostfixExpression(String infixExpression) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(infixExpression, Constants.DELIMITERS, true);
        Stack<String> stack = new Stack<>();
        String funcName = "";
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
                System.err.println(tokenString + " token unexpected !");
                return null;
            }
            if (TokenUtils.isTokenOperand(tokenString)) {
                if (TokenUtils.isTokenMathFunction(tokenString)) {
                    funcFound = true;
                    funcCommaCount = 0;
                    funcName = tokenString;
                }
                sb.append(tokenString.concat(Constants.WHITESPACE));
            } else {
                // token is operator
                Operators tokenOperator = Operators.fromSymbol(tokenString);
                if (tokenOperator == Operators.LEFT_PARENTHESES) {
                    leftParanthesisFound = true;
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


                    if (tokenOperator == Operators.LEFT_PARENTHESES) {
                        if (!TokenUtils.isTokenMathFunction(opOnStack)) {
                            //LEFT PARENTHESES prior operation case
                            stack.push(tokenString);
                            continue;
                        }
                    } else if (tokenOperator == Operators.RIGHT_PARENTHESES) {
                        Operators topOperator = Operators.fromSymbol(stack.peek());

                        if (StackUtils.isBeforeLastOnStackIsLeftParentheses(stack) && !StackUtils.isBeforeLastOnStackIsFunction(stack)) {
                            //sb.append(stack.pop().concat(tr.com.kyilmaz80.myparser.utils.Constants.WHITESPACE));
                            if (topOperator != Operators.LEFT_PARENTHESES) {
                                StringUtils.doAppendOperatorToPostfixExpression(sb, stack.pop().concat(Constants.WHITESPACE));
                            }
                            stack.pop();
                            continue;
                        }
                        //while the operator at the top of the operator stack is not a left parenthesis:
                        while (topOperator != Operators.LEFT_PARENTHESES) {
                            if (stack.isEmpty()) {
                                //return null;
                                System.err.println("Stack empty @RIGHT PARENTHESIS");
                                break;
                            }
                            //pop the operator from the operator stack into the output queue (sb)
                            StringUtils.doAppendOperatorToPostfixExpression(sb, stack.pop().concat(Constants.WHITESPACE));
                            if (stack.isEmpty()) {
                                System.err.println("No left paranthesis left");
                                return null;
                            }
                            topOperator = Operators.fromSymbol(stack.peek());
                        }
                        //pop the left parenthesis from the operator stack and discard it
                        if (topOperator == Operators.LEFT_PARENTHESES) {
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
                        funcCommaCount++;
                        continue;
                    }

                    if (opOnStackOperator == null || tokenOperator == null) {
                        throw new RuntimeException("operator null unexpected!");
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
            System.err.println("Mismatched parentheses problem!");
            return null;
        }
        return sb.toString().trim();
    }
}
