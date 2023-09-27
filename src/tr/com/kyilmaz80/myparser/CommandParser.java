package tr.com.kyilmaz80.myparser;

import tr.com.kyilmaz80.myparser.func.FunctionCalculator;
import tr.com.kyilmaz80.myparser.func.FunctionCalculatorFactory;
import tr.com.kyilmaz80.myparser.func.MathFunction;
import tr.com.kyilmaz80.myparser.func.MultiArgMathFunction;
import tr.com.kyilmaz80.myparser.utils.Operators;
import tr.com.kyilmaz80.myparser.utils.Constants;
import tr.com.kyilmaz80.myparser.utils.StackUtils;
import tr.com.kyilmaz80.myparser.utils.StringUtils;
import tr.com.kyilmaz80.myparser.utils.TokenUtils;

import java.util.Stack;
import java.util.StringTokenizer;

public class CommandParser {
    public static FunctionCalculator fc = FunctionCalculatorFactory.getInstance();
    public static String parse(String command) {
        return convertToPostfixExpression(StringUtils.removeSpaces(command));
    }

    public static Double eval(String postfixExpression) {
        Double result;
        boolean varArgs = false;
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
        while (st.hasMoreElements()) {
            tokenString = TokenUtils.filterToken(st.nextToken());
            if (tokenString.isEmpty()) {
                continue;
            }
            if (TokenUtils.isTokenMathFunction(tokenString)) {
                stack.push(tokenString);
            } else if (TokenUtils.isTokenOperand(tokenString)) {
                if (StackUtils.isBeforeLastOnStackIsFunction(stack)) {
                    String funcStr = StackUtils.doGetBeforeLastOnStack(stack);
                    //if (tr.com.kyilmaz80.myparser.utils.StackUtils.getFunctionArgCount(funcStr) == 2) {
                    MathFunction function = fc.getFunction(funcStr);
                    if (function == null) {
                        throw new RuntimeException(funcStr + " not found!");
                    }
                    int argCount = function.getArgCount();
                    if (argCount == 2) {
                        stack.push(tokenString);
                        Double[] vals = StackUtils.getFuncArgsOnStack(stack, argCount);
                        //result = tr.com.kyilmaz80.myparser.utils.StackUtils.doFuncOperationOnStack(stack, funcStr);
                        result = fc.doCalculation(funcStr, vals[1], vals[0]);
                        stack.pop();
                        stack.push(String.valueOf(result));
                        varArgs = false;
                        continue;
                    //} else if (argCount == -1) {
                    } else if (argCount > 2) {
                        //TODO: are all variadic function arg count > 2 ?
                        stack.push(tokenString);
                        varArgs = true;
                        continue;
                    }
                }
                if (!stack.isEmpty() && TokenUtils.isTokenMathFunction(stack.peek())) {
                    //single valued variable icin
                    String funcStr = stack.peek();
                    MathFunction function = fc.getFunction(funcStr);

                    int argCount = function.getArgCount();

                    //if (argCount == 2 || argCount == -1) {
                    if (argCount >= 2) {
                        varArgs = argCount > 2;
                        stack.push(tokenString);
                        continue;
                    }

                    if (StackUtils.isLastTwoFuncOnStack(stack)) {
                        //nested tr.com.kyilmaz80.myparser.func case
                        stack.push(tokenString);
                    }
                    //push back val for tr.com.kyilmaz80.myparser.func
                    if (TokenUtils.isTokenMathFunction(stack.peek())) {
                        stack.push(tokenString);
                    }
                    Double[] vals = StackUtils.getFuncArgsOnStack(stack, argCount);
                    //TODO: arg may be 1 or >2 assume 1 for now since multifuncarg not implemented yet
                    if (vals.length > 1) {
                        throw new RuntimeException("vals length > 1");
                    }
                    result = fc.doCalculation(funcStr, vals[0]);
                    stack.pop();
                    stack.push(String.valueOf(result));
                } else {
                    stack.push(tokenString);
                }

            } else {
                // token is operator
                // if the two elements on stack are operands
                String funcStr;
                StackUtils.FuncHelper fh;
                //String tokenOperand = null;

                if (varArgs) {
                    if (TokenUtils.isTokenArithmeticOperator(tokenString)) {
                        // multi arg var after a token
                        if (TokenUtils.isTokenMathFunction(stack.peek())) {
                            //tokenOperand = stack.pop();
                            stack.pop();
                        }
                    }
                    fh = StackUtils.getRecursiveBeforeOnStackIsFunction(stack);
                    funcStr = fh.name;
                } else {
                    funcStr = StackUtils.doGetBeforeLastOnStack(stack);
                }

                //no tr.com.kyilmaz80.myparser.func before operator!
                //pow case

                MathFunction function = fc.getFunction(funcStr);

                int argCount = -1;
                if (function != null) {
                    argCount = function.getArgCount();
                }

                if (TokenUtils.isTokenMathFunction(funcStr) && argCount == 2) {
                    //double valued tr.com.kyilmaz80.myparser.func case
                    Double[] vals = StackUtils.getFuncArgsOnStack(stack, argCount);
                    result = fc.doCalculation(funcStr, vals[0], vals[1]);
                    stack.pop();
                    stack.push(String.valueOf(result));
                    //topElementNext = stack.peek();
                    stack.peek();
                }
                else if (TokenUtils.isTokenMathFunction(funcStr)) {

                    //result = StackUtils.doFuncOperationOnStack(stack, funcStr);
                    Double[] vals;
                    if (varArgs) {
                        vals = StackUtils.doGetMultiArgFunctionArgsOnStack(stack, argCount);
                        result = fc.doCalculation(funcStr, vals);
                        stack.push(String.valueOf(result));
                    }else {
                        vals = StackUtils.getFuncArgsOnStack(stack, argCount);
                        result = fc.doCalculation(funcStr, vals[0]);
                        stack.pop();
                        stack.push(String.valueOf(result));
                    }
                    //is remanining operator operation left?
                    if (TokenUtils.isTokenArithmeticOperator(tokenString)) {
                        result = StackUtils.doArithmeticOperationOnStack(stack, tokenString);
                        stack.push(String.valueOf(result));
                    }
                } else {
                    if (StackUtils.isOperationOnStackFunc(stack)) {
                        funcStr = StackUtils.doGetBeforeLastOnStack(stack);
                        Double[] vals = StackUtils.getFuncArgsOnStack(stack, argCount);
                        result = fc.doCalculation(funcStr, vals);
                        //result = tr.com.kyilmaz80.myparser.utils.StackUtils.doFuncOperationOnStack(stack, funcStr);
                        //pop the tr.com.kyilmaz80.myparser.func
                        stack.pop();
                    } else {
                        result = StackUtils.doArithmeticOperationOnStack(stack, tokenString);
                        if (result == -1) {
                            //error condition unexpected operator
                            //not a +-/* operator
                            //System.exit(1);
                            return null;
                        }
                    }
                    stack.push(String.valueOf(result));
                }
            }
        }
        while (stack.size() != 1) {
            if (StackUtils.isOperationOnStackArithmetic(stack) && TokenUtils.isTokenArithmeticOperator(tokenString)) {
                result = StackUtils.doArithmeticOperationOnStack(stack, tokenString);
            }
            else {
                //single arg tr.com.kyilmaz80.myparser.func case
                MathFunction function;
                int argCount;
                Double[] vals;
                if (!varArgs) {
                    function = fc.getFunction(tokenString);
                    if (function == null) {
                        throw new RuntimeException(tokenString + " not found!");
                    }
                    argCount = function.getArgCount();
                    vals = StackUtils.getFuncArgsOnStack(stack, argCount);
                    result = fc.doCalculation(tokenString, vals[0]);
                }else {
                    StackUtils.FuncHelper fh = StackUtils.getRecursiveBeforeOnStackIsFunction(stack);
                    function =  fc.getFunction(fh.name);
                    argCount = function.getArgCount();
                    vals = StackUtils.doGetMultiArgFunctionArgsOnStack(stack, argCount);
                    result = fc.doCalculation(fh.name, vals);
                }
            }
            stack.push(String.valueOf(result));
        }
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
