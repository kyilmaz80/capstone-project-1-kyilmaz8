import func.*;
import java.lang.reflect.*;
public class Functions {
    private int functionCount;
    private int currentCount = 0;

    private MathFunction[] functions;
    private double argument;

    public Functions(int functionCount) {
        this.functionCount = functionCount;
        functions = new MathFunction[functionCount];
    }

    public void addFunction(MathFunction function) {
        functions[currentCount] = function;
        currentCount++;
    }

    public double doCalculation(String functionName, double arg) {
        double result = 0.0;
        boolean isFunctionFound = false;
        for (MathFunction function : functions) {
            if (functionName.equalsIgnoreCase(function.getName())) {
                result = ((SingleArgMathFunction)function).calculate(arg);
                isFunctionFound = true;
            }
        }
        if(!isFunctionFound)
            System.out.println("No such function found!");

        return result;
    }

    public double doCalculation(String functionName, double arg1, double arg2) {
        double result = 0.0;
        boolean isFunctionFound = false;
        for (MathFunction function : functions) {
            if (functionName.equalsIgnoreCase(function.getName())) {
                result = ((DoubleArgMathFunction)function).calculate(arg1, arg2);
                isFunctionFound = true;
            }
        }
        if(!isFunctionFound)
            System.out.println("No such function found!");

        return result;
    }

    public void listMathFunction() {
        System.out.println("Available Functions:");
        for (MathFunction function : functions)
            System.out.println(function.getName());
    }



    public Functions() {
        /*
        //TODO: Cos burada Constants.java'daki allowed_math_functions'dan gelecek
        clazz = Class.forName("func." + "Cos" + "Function");

        Object o = clazz.getDeclaredConstructor().newInstance();
        mf = (MathFunction) castObject(clazz, o);
        CosFunction cf = (CosFunction) mf;
        System.out.println(cf.calculate(0));
         */
        /*
        for (String functionName : Constants.ALLOWED_MATH_FUNCTIONS) {
            try {
                // Dynamically create the class name based on the function name
                String className = "func." + functionName.substring(0, 1).toUpperCase() + functionName.substring(1) + "Function";

                // Load the class
                Class<?> clazz = Class.forName(className);

                // Create an instance of the class
                Object obj = clazz.getDeclaredConstructor().newInstance();

                // Check if the object is an instance of MathFunction
                if (obj instanceof MathFunction) {
                    MathFunction mf = (MathFunction) obj;
                    if (mf instanceof SingleArgMathFunction) {
                        SingleArgMathFunction saf = (SingleArgMathFunction) mf;
                        double result = saf.calculate(0); // Replace 0 with the desired argument
                        System.out.println(functionName + "(0) = " + result);
                    } else {
                        System.out.println(functionName + " is not a SingleArgMathFunction.");
                    }
                } else {
                    System.out.println(functionName + " is not a MathFunction.");
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

         */



        /*
        Calculator calculator = new FlexCalculator(Constants.ALLOWED_MATH_FUNCTIONS.length);

        Map<String, MathFunction> functions = new HashMap<>();
        for (String func : Constants.ALLOWED_MATH_FUNCTIONS) {
            functions.put(func, MathFunction.)
        }

         */
    }

    public static void main(String[] args) {
        //Functions f = new Functions();
        Calculator calculator1 = new FlexCalculator(Constants.ALLOWED_MATH_FUNCTIONS.length);
        for (String functionName : Constants.ALLOWED_MATH_FUNCTIONS) {
            String className = "func." + functionName.substring(0, 1).toUpperCase() + functionName.substring(1) + "Function";

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
                    calculator1.addFunction(mf);
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

        calculator1.listMathFunction();


    }
}
