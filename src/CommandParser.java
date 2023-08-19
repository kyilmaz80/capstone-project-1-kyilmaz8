import java.util.Arrays;
import java.util.Stack;
import java.util.StringTokenizer;

public class CommandParser {
    private static Stack operatorsStack = new Stack();
    private static Stack operandsStack = new Stack();
    public static String parse(String command) {
        //TODO: lint tarzi sentaks kontrolu
        //StringTokenizer i +- icin sırayla recursive çalıştırarak islem yapılabili
        //parse tree lere bakılabilir
        StringTokenizer st = new StringTokenizer(command, Constants.DELIMITERS, true);
        while(st.hasMoreElements()) {
            String tokenString = filterToken(st.nextToken());
            //TODO: operanda gelmişse pop layıp isle
            if (isTokenNumerical(tokenString)) {
                operandsStack.push(tokenString);
            } else {
                operatorsStack.push(tokenString);
            }
            if (!isTokenValid(filterToken(tokenString))) {
                return null;
            }
        }
        return command;
    }

    public static double execute(String command) {
        //TODO: komutun parse edilmis halinin hesabi
        String op;
        double val1;
        double val2;
        double result = 0;
        Stack tempOperandsStack = new Stack();
        Stack tempOperatorsStack = new Stack();

        while(!operandsStack.isEmpty()) {
            boolean opNotPrior = false;
            if (operatorsStack.isEmpty()) {
                result = Double.parseDouble(operandsStack.pop().toString());
                break;
            }
            int opStackSize = operatorsStack.size();
            /*
            //buggy kisim
            op = operatorsStack.pop().toString();
            if (!isOperatorPrior(op) && opStackSize > 1) {
                String opNext = operatorsStack.pop().toString();
                operatorsStack.push(opNext);
                if (!isOperatorPrior(opNext)) {
                    tempOperands.push(operandsStack.pop());
                    tempOperands.push(operandsStack.pop());
                    tempOperators.push(op);
                    op = operatorsStack.pop().toString();
                    opPrior = true;
                }else {
                    tempOperands.push(operandsStack.pop());
                    tempOperators.push(op);
                    op = operatorsStack.pop().toString();
                }
            }
            */
            //TODO: son 2 operatore bakmak gerekiyor
            op = operatorsStack.pop().toString();
            Operators opEnum = Operators.fromSymbol(op);
            //String opNext = operatorsStack.pop().toString();
            //op = getPriorOperator(opCurrent, opNext);
            if (opStackSize > 1) {
                String opNext = operatorsStack.pop().toString();
                operatorsStack.push(opNext);
                Operators opEnumNext = Operators.fromSymbol(opNext);
                //if (!isOperatorPrior(op, operatorsStack) && opStackSize > 1) {
                if (!opEnum.isOperatorPrior(opEnumNext) && opStackSize > 1) {
                    tempOperatorsStack.push(op);
                    tempOperandsStack.push(operandsStack.pop());
                    //op = operatorsStack.pop().toString();
                    opNotPrior = true;
                    continue;
                }
            }

            val1 = Double.valueOf(operandsStack.pop().toString());
            val2 = Double.valueOf(operandsStack.pop().toString());
            result = doOperation(val1, val2, op);
            /*
            //buggy
            operandsStack.push(result);
            if (opPrior) {
                operatorsStack.push(tempOperators.pop());
                operandsStack.push(tempOperands.pop());
                operandsStack.push(tempOperands.pop());
            }
            */
            if (!opNotPrior) {
                if (tempOperandsStack.size() == 0 ) {
                    operandsStack.push(result);
                    continue;
                }
            }

            tempOperandsStack.push(result);
            while(!tempOperandsStack.isEmpty()) {
                if (tempOperandsStack.size() == 1) {
                    result = Double.parseDouble(tempOperandsStack.pop().toString());
                    break;
                }

                val1 = Double.valueOf(tempOperandsStack.pop().toString());
                val2 = Double.valueOf(tempOperandsStack.pop().toString());
                op = tempOperatorsStack.pop().toString();
                result = doOperation(val1, val2, op);
                tempOperandsStack.push(result);
            }

        }
        return result;
    }

    private static double doOperation(double val1, double val2, String op) {
        double result = -1;
        if (op.equals("*")) {
            result = val2 * val1;
        } else if (op.equals("/")) {
            result = val2 / val1;
        } else if (op.equals("+")) {
            result = val2 + val1;
        } else if (op.equals("-")) {
            result = val2 - val1;
        } else {
            //TODO: pass
            System.out.println("NOT IMPLEMENTED!");
        }
        return result;
    }

    private static boolean isTokenNumerical(String str) {
        if (str.equals("") || str == null) {
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
        String[] funcArray = str.split("\\(");
        return Arrays.binarySearch(Constants.ALLOWED_MATH_FUNCTIONS, funcArray[0].toLowerCase()) >= 0;
    }

    private static boolean isTokenValid(String token) {
        return isTokenExit(token) || isTokenNumerical(token) || isTokenMathFunction(token) || isTokenDelimiter(token);
    }

    private static boolean isTokenExit(String token) {
        return token.toLowerCase().equals(Constants.COMMAND_EXIT);
    }

    private static String filterToken(String token) {
        return token.trim();
    }
    
    private static boolean isTokenDelimiter(String token) {
        return Constants.DELIMITERS.indexOf(token) >= 0;
    }

    /*
    private static boolean isOperatorPrior(String op1, Stack stack) {
        //return op.equals("*") || op.equals("/");

        return op1.equals(getPriorOperator(stack));
    }

    private static String getPriorOperator(String op1, String op2) {
        String op = op2;
        if (op1.equals("*") && op2.equals("/")) {
            op = op1;
        } else if ((op1.equals("*") || op2.equals("/")) && (op1.equals("+") || op2.equals("-"))) {
            op = op1;
        }else if (op1.equals("+") && op2.equals("-")) {
            op = op1;
        }
        return op;
    }

    private static String getPriorOperator(Stack stack) {
        String op1 = stack.pop().toString();
        String op2 = stack.pop().toString();
        stack.push(op2);
        stack.push(op1);
        return getPriorOperator(op1, op2);

    }
     */
}
