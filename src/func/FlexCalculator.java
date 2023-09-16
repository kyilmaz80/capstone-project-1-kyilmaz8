package func;

import java.lang.reflect.InvocationTargetException;

public class FlexCalculator implements Calculator{
    private int functionCount;
    private int currentCount = 0;
    private MathFunction[] functions;
    private double argument;

    public FlexCalculator(int functionCount) {
        this.functionCount = functionCount;
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
    public double doCalculation(String functionName, double[] args) {
        throw new RuntimeException("Multi args not implemented yet!");
    }

    public MathFunction findFunction(String functionName) {
        //TODO: hashmap version
        for (MathFunction function : functions) {
            if (functionName.equalsIgnoreCase(function.getName())) {
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
            String className = "func." + functionName.substring(0, 1).toUpperCase()
                    + functionName.substring(1) + "Function";

            Class<?> clazz = null;
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
                    /*
                    if (mf instanceof SingleArgMathFunction) {
                        SingleArgMathFunction saf = (SingleArgMathFunction) mf;
                        calculator1.addFunction(saf);
                        //double result = saf.calculate(0); // Replace 0 with the desired argument
                        //System.out.println(functionName + "(0) = " + result);
                    }else if(mf instanceof DoubleArgMathFunction) {
                        DoubleArgMathFunction daf = (DoubleArgMathFunction) mf;
                        calculator1.addFunction(daf);
                    } else {
                        System.out.println(functionName + " is not implemented as a ArgMathFunction.");
                    }
                     */
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
