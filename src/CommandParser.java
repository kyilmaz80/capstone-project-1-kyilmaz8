import java.util.Arrays;
import java.util.StringTokenizer;

public class CommandParser {
    public static String parse(String command) {
        //TODO: lint tarzi sentaks kontrolu
        //StringTokenizer i +- icin sırayla recursive çalıştırarak islem yapılabili
        //parse tree lere bakılabilir
        StringTokenizer st = new StringTokenizer(command, "+-");
        while(st.hasMoreElements()) {
            if (!isTokenValid(st.nextToken())) {
                return null;
            }
        }
        return command;
    }

    public static int execute(String command) {
        //TODO: komutun parse edilmis halinin hesabi
        return 0;
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
        return isTokenExit(token) || isTokenNumerical(token) || isTokenMathFunction(token);
    }

    private static boolean isTokenExit(String token) {
        return token.toLowerCase().equals(Constants.COMMAND_EXIT);
    }
}
