package tr.com.kyilmaz80.myparser;
import tr.com.kyilmaz80.myparser.utils.Constants;

public class Main {
    public static void main(String[] args) {

        Menu.printHeader();
        String command;

        do {
            command = Menu.getInput();
            if (command.equalsIgnoreCase(Constants.COMMAND_EXIT)) {
                break;
            }
            String parsedCommand = CommandParser.parse(command);
            if (parsedCommand == null) {
                String message =  Bundle.get().getString("main_err1");
                System.out.println(message);
                continue;
            }
            Menu.printOutput();
            Double result = CommandParser.eval(parsedCommand);
            System.out.println(result);
        } while (true);
        Menu.exit();
    }
}