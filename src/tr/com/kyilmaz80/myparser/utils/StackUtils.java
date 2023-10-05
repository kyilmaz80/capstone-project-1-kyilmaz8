package tr.com.kyilmaz80.myparser.utils;
import tr.com.kyilmaz80.myparser.func.*;

import java.util.Stack;
import java.util.StringTokenizer;

public class StackUtils {
    public static FunctionCalculator fc = FunctionCalculatorFactory.getInstance();

    public static double doOperation(double val1, double val2, Operators op) {
        double result = -1;
        switch (op) {
            case MULTIPLICATION -> result = val2 * val1;
            case DIVISION -> result = val2 / val1;
            case ADDITION -> result = val2 + val1;
            case SUBTRACTION -> result = val2 - val1;
            default -> System.out.println(op + " OP NOT IMPLEMENTED doOperation!");
        }
        return result;
    }

    public static double doArithmeticOperationOnStack(Stack<String> stack, String tokenString) {
        if (stack.isEmpty()) {
            throw new RuntimeException("Stack empty!");
        }

        if (stack.size() < 2) {
            throw new RuntimeException("Stack size too small for arithmetic operation!");
        }

        double val1 = Double.parseDouble(stack.pop());
        //can be operand or tr.com.kyilmaz80.myparser.func
        double val2 = Double.parseDouble(stack.pop());

        Operators operator = Operators.fromSymbol(tokenString);
        if (operator == null) {
            throw new RuntimeException("Operator null unexpected!");
        }
        return StackUtils.doOperation(val1, val2, operator);
        //stack.push(String.valueOf(result));
    }

    public static boolean isLastTwoNumOnStack(Stack<String> stack) {
        if (stack.size() < 2) {
            return false;
        }
        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);

        return TokenUtils.isTokenNumerical(op1) && TokenUtils.isTokenNumerical(op2);
    }

    public static boolean isBeforeLastOnStackIsFunction(Stack<String> stack) {
        if (stack.size() < 2) {
            return false;
        }
        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);

        return TokenUtils.isTokenMathFunction(op2);
    }

    public static String getBeforeLastFunctionOnStack(Stack<String> stack) {
        if (stack.size() < 2) {
            return null;
        }

        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);

        return op2;
    }

    public static boolean doCalculateFuncOnStack(Stack<String> stack, MathFunction mf, StringTokenizer st) {
        Double calcVal;
        String numVal = null;

        //pop the val
        if (TokenUtils.isTokenNumerical(stack.peek())) {
            numVal = stack.pop();
        }
        //pop the func
        if(TokenUtils.isTokenMathFunction(stack.peek())) {
            stack.pop();
        }

        calcVal = getCalculateFuncOnStack(mf, st, numVal);
        if (calcVal == null) {
            return false;
        }
        stack.push(calcVal.toString());

        return true;
    }

    public static Double getCalculateFuncOnStack(MathFunction mf, StringTokenizer st, String numVal)  {
        Double calcVal;
        if (mf instanceof SingleArgMathFunction samf) {
            double val = Double.parseDouble(numVal);
            calcVal = samf.calculate(val);
        }else if (mf instanceof DoubleArgMathFunction damf && damf.getArgCount() == 2) {
            double val = Double.parseDouble(numVal);
            String nextToken = TokenUtils.getFilterNextToken(st);
            double nextVal = Double.parseDouble(nextToken);
            calcVal = damf.calculate(val, nextVal);
        }else if (mf instanceof MultiArgMathFunction mamf) {
            int count = mamf.getArgCount();
            Double[] vals = new Double[count];
            vals[0] = Double.parseDouble(numVal);
            for (int i = 1; i < count; i++) {
                String nextToken = TokenUtils.getFilterNextToken(st);
                if (TokenUtils.isTokenNumerical(nextToken)) {
                    vals[i] = Double.parseDouble(nextToken);
                }else {
                    MathFunction mf2 = fc.getFunction(nextToken);
                    vals[i] = getCalculateFuncOnStack(mf2, st, TokenUtils.getFilterNextToken(st));
                }
            }
            calcVal = mamf.calculate(vals);
        }else {
            System.err.println("NOT IMPLEMENTED FUNC TYPE!");
            return null;
        }
        return calcVal;
    }
    public static boolean doCalculateArithmeticOnStack(Stack<String> stack, String arithmeticOperator) {
        if (StackUtils.isLastTwoNumOnStack(stack)) {
            Double result = StackUtils.doArithmeticOperationOnStack(stack, arithmeticOperator);
            stack.push(result.toString());
            //opStack.pop();
            return true;
        }
        return false;
    }

    public static boolean isBeforeLastOnStackIsLeftParentheses(Stack<String> stack) {
        if (stack.size() < 2) {
            return false;
        }
        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);

        return Operators.fromSymbol(op2) == Operators.LEFT_PARENTHESES;
    }


}
