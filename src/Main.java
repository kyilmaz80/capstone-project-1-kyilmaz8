public class Main {
    public static void main(String[] args) {
        Menu.printHeader();
        String command;
        do {
            command = Menu.getInput();
            String parsedCommand = CommandParser.parse(command);
            if (parsedCommand == null) {
                System.out.println("Komut parse edilemedi!");
                continue;
            }
            Menu.printOutput();
            double result = CommandParser.execute(parsedCommand);
            System.out.println(result);
        } while (!command.equals(Constants.COMMAND_EXIT));
        Menu.exit();
    }
}