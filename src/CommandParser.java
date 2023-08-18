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
        Stack temp = new Stack();
        while(!operandsStack.isEmpty()) {
            boolean opPrior = false;
            if (operatorsStack.isEmpty()) {
                result = Double.parseDouble(operandsStack.pop().toString());
                break;
            }
            int opStackSize = operatorsStack.size();
            op = operatorsStack.pop().toString();
            if (!isOperatorPrior(op) && opStackSize > 1) {
                temp.push(operandsStack.pop());
                temp.push(op);
                op = operatorsStack.pop().toString();
                opPrior = true;
            }
            val1 = Double.valueOf(operandsStack.pop().toString());
            val2 = Double.valueOf(operandsStack.pop().toString());
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
            operandsStack.push(result);
            if (opPrior) {
                operatorsStack.push(temp.pop());
                operandsStack.push(temp.pop());
            }


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

    private static boolean isOperatorPrior(String op) {
        return op.equals("*") || op.equals("/");
    }
}
