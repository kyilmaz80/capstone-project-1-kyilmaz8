package tr.com.kyilmaz80.myparser;
import tr.com.kyilmaz80.myparser.func.FunctionCalculator;
import tr.com.kyilmaz80.myparser.func.FunctionCalculatorFactory;
import tr.com.kyilmaz80.myparser.utils.StringUtils;
import java.util.Scanner;

public class Menu {
    public static void printHeader() {
        System.out.println(Bundle.get().getString("menu_header1"));
        System.out.println(Bundle.get().getString("menu_header2"));
        //System.out.println(Bundle.get().getString("menu_header3"));
        FunctionCalculatorFactory.getInstance().listMathFunction();
        System.out.println(Bundle.get().getString("menu_header4"));
    }

    public static String getInput() {
        System.out.print(Bundle.get().getString("menu_print1"));
        Scanner scanner = new Scanner(System.in);
        String prompt = scanner.nextLine();
        return StringUtils.removeSpaces(prompt.toLowerCase());
    }

    public static void exit() {
        System.out.println(Bundle.get().getString("menu_print2"));
    }

    public static void printOutput() {
        System.out.print(Bundle.get().getString("menu_print3"));
    }
}
