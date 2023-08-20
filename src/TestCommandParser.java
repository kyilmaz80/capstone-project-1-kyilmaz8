public class TestCommandParser {
    public static void main(String[] args) {
        assert CommandParser.parse("5+6*7").equals("567*+");
        assert CommandParser.parse("5*6+7").equals("56*7+");
        assert CommandParser.parse("5/4*3+2").equals("54/3*2+");
        assert CommandParser.parse("5/4-3*2").equals("54/32*-");
    }
}
