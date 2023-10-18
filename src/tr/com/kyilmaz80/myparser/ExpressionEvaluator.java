package tr.com.kyilmaz80.myparser;

import tr.com.kyilmaz80.myparser.func.MathFunction;
import tr.com.kyilmaz80.myparser.utils.Constants;
import tr.com.kyilmaz80.myparser.utils.StackUtils;
import tr.com.kyilmaz80.myparser.utils.TokenUtils;

import java.util.Stack;
import java.util.StringTokenizer;

public class ExpressionEvaluator {
    public static Double eval(String postfixExpression) {
        //Double result;
        Stack<String> stack = new Stack<>();
        Stack<String> opStack = new Stack<>();
        String tokenString;
        if (postfixExpression == null) {
            System.err.println(Bundle.get().getString("cp_err1"));
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
                    MathFunction mf = ExpressionParser.fc.getFunction(stack.peek());
                    if (mf == null) {
                        System.err.println(Bundle.get().getString("cp_err2"));
                        continue;
                    }

                    if (TokenUtils.isTokenNumerical(tokenString)) {
                        stack.push(tokenString);
                    }

                    //recursive case
                    while(StackUtils.isBeforeLastOnStackIsFunction(stack)) {

                        String funcName = StackUtils.getBeforeLastFunctionOnStack(stack);
                        mf = ExpressionParser.fc.getFunction(funcName);
                        if (!StackUtils.doCalculateFuncOnStack(stack, mf, st)) {
                            throw new RuntimeException(Bundle.get().getString("cg_err3"));
                        }
                        //System.out.println("before a func!");
                        // if there is an operation on nested func value, break!
                        if (st.hasMoreElements()) {
                            break;
                        }
                    }
                }
            } else if(TokenUtils.isTokenMathFunction(tokenString)) {
                stack.push(tokenString);
            }else if (TokenUtils.isTokenArithmeticOperator(tokenString)) {
                //pop the top two operands
                //if the two operands are numerical
                // do arithmetical operation
                opStack.push(tokenString);
                if (StackUtils.isLastTwoNumOnStack(stack)) {
                    if (!StackUtils.doCalculateArithmeticOnStack(stack, tokenString)){
                        throw new RuntimeException(Bundle.get().getString("cp_err4"));
                    }
                    opStack.pop();
                } else {
                    // before peek is a function
                    String funcName = StackUtils.getBeforeLastFunctionOnStack(stack);
                    MathFunction mf;

                    //recursive case
                    while(StackUtils.isBeforeLastOnStackIsFunction(stack)){
                        mf = ExpressionParser.fc.getFunction(funcName);
                        //System.out.println("before a func!");
                        if (!StackUtils.doCalculateFuncOnStack(stack, mf, st)) {
                            throw new RuntimeException(Bundle.get().getString("cp_err3"));
                        }
                    }
                    //do the remaining arithmetic
                    if (!opStack.isEmpty()) {
                        if (StackUtils.isLastTwoNumOnStack(stack)) {
                            if (!StackUtils.doCalculateArithmeticOnStack(stack, opStack.pop())){
                                throw new RuntimeException(Bundle.get().getString("cp_err4"));
                            }
                        }
                    }
                }

            }
        }
       // System.out.println(stack.size());
        return Double.parseDouble(stack.pop());
    }
}
