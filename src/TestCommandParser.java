public class TestCommandParser {
    public static void main(String[] args) {
        //test1();
        //test2();
        test3();
    }

    public static void test1() {
        assert CommandParser.parse("5+6*7").equals("567*+");
        assert CommandParser.parse("5*6+7").equals("56*7+");
        assert CommandParser.parse("5/4*3+2").equals("54/3*2+");
        assert CommandParser.parse("5/4-3*2").equals("54/32*-");
    }

    public static void test2() {
        assert CommandParser.execute(CommandParser.parse("5+6*7")) == 47.0;
        assert CommandParser.execute(CommandParser.parse("5*6+7")) == 37.0;
        assert CommandParser.execute(CommandParser.parse("5/4*3+2")) == 5.75;
        assert CommandParser.execute(CommandParser.parse("5/4-3*2")) == -4.75;
    }

    public static void test3() {
        assert CommandParser.parse("5*4+cos(0)").equals("54*cos0+");
    }
}
