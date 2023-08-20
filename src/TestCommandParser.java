public class TestCommandParser {
    public static void main(String[] args) {
        assert CommandParser.parse("5+6*7").equals("567*+");
        //String p2 = CommandParser.parse("5*6+7");
        //System.out.println(p2);
        assert CommandParser.parse("5*6+7").equals("56*7+");
        //String p = CommandParser.parse("5*6+7");
        //System.out.println(p);
    }
}
