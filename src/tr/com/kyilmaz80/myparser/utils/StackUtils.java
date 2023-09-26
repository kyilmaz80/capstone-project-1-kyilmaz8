package tr.com.kyilmaz80.myparser.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StackUtils {
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

    //implementation migrated to new FunctionsFactory
    @Deprecated
    public static double doCalculateFunction(String funcStr, double[] args) {
        double result = -1;
        //String[] funcArray = funcStr.split("\\(");
        //String tr.com.kyilmaz80.myparser.func = funcArray[0].toLowerCase();
        //double val = double.valueOf(funcs);
        switch (funcStr) {
            case "cos" -> result = Math.cos(args[0]);
            case "sin" -> result = Math.sin(args[0]);
            case "pow" -> result = Math.pow(args[1], args[0]);
            case "sqrt" -> result = Math.sqrt(args[0]);
            default -> System.out.println("FUNC NOT IMPLEMENTED!");
        }
        return result;
        //return Arrays.binarySearch(tr.com.kyilmaz80.myparser.utils.Constants.ALLOWED_MATH_FUNCTIONS, funcArray[0].toLowerCase()) >= 0;
    }


    public static double doArithmeticOperationOnStack(Stack<String> stack, String tokenString) {
        double val1 = Double.parseDouble(stack.pop());
        //can be operand or tr.com.kyilmaz80.myparser.func
        double val2 = Double.parseDouble(stack.pop());

        Operators operator = Operators.fromSymbol(tokenString);
        return StackUtils.doOperation(val1, val2, operator);
        //stack.push(String.valueOf(result));
    }

    public static Double[] getFuncArgsOnStack(Stack<String> stack, int varCount) {
        Double[] vals = new Double[varCount];
        for (int i = 0; i < varCount; i++) {
            vals[i] = Double.parseDouble(stack.pop());
        }
        return vals;
    }

    public static Double[] getFuncArgsOnStack(Stack<String> stack) {
        //double[] vals = new double[varCount];
        List<Double> arrayList = new ArrayList();
        while(TokenUtils.isTokenNumerical(stack.peek())) {
            //TODO: stack empty case?
            Double v = Double.parseDouble(stack.pop());
            arrayList.add(v);
        }
        Double[] vals = new Double[arrayList.size()];
        vals = arrayList.toArray(vals);
        return vals;
    }


    //implementation migrated to new FunctionsFactory
    @Deprecated
    public static double doFuncOperationOnStack(Stack<String> stack, String funcStr) {

        int varCount = getFunctionArgCount(funcStr);
        double[] vals = new double[varCount];
        for (int i = 0; i < varCount; i++) {
            vals[i] = Double.parseDouble(stack.pop());
        }
        //disregard the tr.com.kyilmaz80.myparser.func
        //stack.pop();
        //stack.push(String.valueOf(result));
        return doCalculateFunction(funcStr, vals);
    }


    public static boolean isLastTwoFuncOnStack(Stack<String> stack) {
        if (stack.size() < 2) {
            return false;
        }
        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);

        return TokenUtils.isTokenMathFunction(op1) && TokenUtils.isTokenMathFunction(op2);
    }

    @Deprecated
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

    public static class FuncHelper {
        public boolean isFunction;
        public int funcArgCount;
        public String name;
        FuncHelper(boolean isFunction, int funcArgCount, String name) {
            this.isFunction = isFunction;
            this.funcArgCount = funcArgCount;
            this.name = name;
        }
    }
    public static FuncHelper getRecursiveBeforeOnStackIsFunction(Stack<String> stack) {
        String op;
        List<String> arrayList = new ArrayList();
        if (stack.isEmpty()) {
            return new FuncHelper(false, 0, null);
        }
        op = stack.pop();
        if (stack.size() < 2) {
            return new FuncHelper(false, 0, null);
        }
        while(TokenUtils.isTokenNumerical(op)) {
            if (stack.isEmpty()) {
                //throw new RuntimeException("Stack empty in isRecursiveBeforeOnStackIsFunction");
                break;
            }
            arrayList.add(op);
            op = stack.pop();
        }
        String name = op;
        boolean isFunc = TokenUtils.isTokenMathFunction(name);

        stack.push(op);
        for(int i = arrayList.size()-1; i >= 0; i--) {
            stack.push(arrayList.get(i));
        }

        return new FuncHelper(isFunc, arrayList.size(), name);
    }

    public static Double[] doGetMultiArgFunctionArgsOnStack(Stack<String> stack, int argCount) {
        String op;

        List<Double> arrayList = new ArrayList();
        if (stack.isEmpty()) {
            return null;
        }
        op = stack.pop();

        while(!TokenUtils.isTokenMathFunction(op)) {
            if (stack.isEmpty()) {
                //throw new RuntimeException("Stack empty in isRecursiveBeforeOnStackIsFunction");
                break;
            }
            Double v = Double.parseDouble(op);
            arrayList.add(v);
            op = stack.pop();
        }
        String name = op;
        if (!TokenUtils.isTokenMathFunction(op)) {
            throw new RuntimeException("multi arg func not came!");
        }

        //stack.push(op);
        int sz = arrayList.size();
        for(int i = sz-1; i >= 0; i--) {

            //sondan 3 ünü al size - 3 den küçükleri remove
            if (i < (arrayList.size() - argCount)) {
                stack.push(arrayList.get(i).toString());
                arrayList.remove(i);

            }
        }

        Double[] vals = new Double[arrayList.size()];
        vals = arrayList.toArray(vals);

        return vals;
    }

    @Deprecated
    public static String doGetBeforeLastOnStack(Stack<String> stack) {
        if (stack.size() < 2) {
            return null;
        }
        String op1 = stack.pop();
        String op2 = stack.pop();
        stack.push(op2);
        stack.push(op1);
        return op2;
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

    public static boolean isOperationOnStackArithmetic(Stack<String> stack) {
        if (stack.size() < 2) {
            return false;
        }
        String val1 = stack.pop();
        String val2 = stack.pop();
        stack.push(val2);
        stack.push(val1);

        return TokenUtils.isTokenNumerical(val1) && TokenUtils.isTokenNumerical(val2);
    }

    public static boolean isOperationOnStackFunc(Stack<String> stack) {
        return !isOperationOnStackArithmetic(stack);
    }

    public static int getFunctionArgCount(String funcStr) {
        int result = 0;
        if (funcStr == null || funcStr.equalsIgnoreCase("")) {
            return -1;
        }
        //TODO: Functions class i ekleyip tr.com.kyilmaz80.myparser.utils.Constants'dan otomatik almak gerekebilir.
        switch (funcStr) {
            case "cos", "sin", "sqrt" -> result = 1;
            case "pow" -> result = 2;
            default -> System.out.println("NOT IMPLEMENTED!");
        }
        return result;
    }
}
