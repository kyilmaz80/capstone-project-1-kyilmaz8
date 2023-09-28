package tr.com.kyilmaz80.myparser.func;

import tr.com.kyilmaz80.myparser.utils.StringUtils;
import tr.com.kyilmaz80.myparser.utils.TokenUtils;

import java.lang.reflect.InvocationTargetException;
public class FunctionCalculator implements Calculator{
    private int currentCount = 0;
    private final MathFunction[] functions;

    public FunctionCalculator(int functionCount) {
        functions = new MathFunction[functionCount];
        initMathFunctions();
    }

    @Override
    public double doCalculation(String functionName, double arg) {
        double result = 0.0;
        boolean isFunctionFound = false;

        MathFunction function = findFunction(functionName);
        if (function != null ) {
            result = ((SingleArgMathFunction)function).calculate(arg);
            isFunctionFound = true;
        }

        if(!isFunctionFound)
            System.err.println("No such function found!");

        return result;
    }

    @Override
    public double doCalculation(String functionName, double arg1, double arg2) {
        double result = 0.0;
        boolean isFunctionFound = false;

        MathFunction function = findFunction(functionName);
        if (function != null ) {
            result = ((DoubleArgMathFunction)function).calculate(arg1, arg2);
            isFunctionFound = true;
        }

        if(!isFunctionFound)
            System.out.println("No such function found!");

        return result;
    }

    @Override
    public Double doCalculation(String functionName, Double[] args) {
        double result = 0.0;
        boolean isFunctionFound = false;

        MathFunction function = findFunction(functionName);
        if (function != null ) {
            result = ((MultiArgMathFunction)function).calculate(args);
            isFunctionFound = true;
        }

        if(!isFunctionFound)
            System.out.println("No such function found!");

        return result;
    }

    public MathFunction findFunction(String functionName) {
        //TODO: hashmap version
        for (MathFunction function : functions) {
            String num = functionName.substring(functionName.length()-1);

            String functionNameStr = StringUtils.removeNumbers(functionName);

            if (functionNameStr.equalsIgnoreCase(function.getName())) {

                if (TokenUtils.isTokenNumerical(num)) {
                    if (function instanceof MultiArgMathFunction maf) {
                        maf.setArgCount(Integer.parseInt(num));
                    }
                }

               return function;
            }
        }
        return null;
    }
    public MathFunction getFunction(String functionName) {
        return findFunction(functionName);
    }
    @Override
    public void addFunction(MathFunction function) {
        functions[currentCount] = function;
        currentCount++;
    }

    @Override
    public MathFunction[] getFunctions() {
        return functions;
    }

    public void initMathFunctions() {
        for (String functionName : FunctionConstants.ALLOWED_MATH_FUNCTIONS) {
            String functionNameStr = StringUtils.removeNumbers(functionName);
            String className = "tr.com.kyilmaz80.myparser.func." + functionNameStr.substring(0, 1).toUpperCase()
                    + functionNameStr.substring(1) + "Function";

            Class<?> clazz;
            try {
                // Load the class
                clazz = Class.forName(className);
            }catch(ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(className + " class not implemented!");
            }

            try{
                // Create an instance of the class
                Object obj = clazz.getDeclaredConstructor().newInstance();
                if (obj instanceof MathFunction) {
                    MathFunction mf = (MathFunction) obj;
                    this.addFunction(mf);
                } else {
                    System.out.println(functionName + " is not a MathFunction.");
                }
                //calculator1.addFunction();
            }catch (InstantiationException | IllegalAccessException |
                    NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void listMathFunction() {
        System.out.println("Available Functions:");
        for (MathFunction function : functions)
            System.out.println(function.getName());
    }
}
