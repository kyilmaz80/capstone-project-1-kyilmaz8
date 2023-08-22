public class TestCommandParser {
    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
    }

    public static void test1() {
        assert CommandParser.parse("5+6*7").equals("5 6 7 * +");
        assert CommandParser.parse("5*6+7").equals("5 6 * 7 +");
        assert CommandParser.parse("5/4*3+2").equals("5 4 / 3 * 2 +");
        assert CommandParser.parse("5/4-3*2").equals("5 4 / 3 2 * -");
    }

    public static void test2() {
        assert CommandParser.eval(CommandParser.parse("5+6*7")) == 47.0;
        assert CommandParser.eval(CommandParser.parse("5*6+7")) == 37.0;
        assert CommandParser.eval(CommandParser.parse("5/4*3+2")) == 5.75;
        assert CommandParser.eval(CommandParser.parse("5/4-3*2")) == -4.75;
    }

    public static void test3() {
        assert CommandParser.eval(CommandParser.parse("5*4+cos(0)")) == 21.0;
        assert CommandParser.eval(CommandParser.parse("5*4+cos(0)*5")) == 25.0;

        String exp = CommandParser.parse("5*4+sqrt(cos(0)*25)");
        System.out.println(exp);
        Double res = CommandParser.eval(exp);
        System.out.println(res);
        //5 4 * sqrt cos 0 25 * +
        assert CommandParser.eval(exp) == 25.0;
        //String postfix = CommandParser.parse("5*6+7");
        //System.out.println(postfix);
        //Double val = CommandParser.eval(postfix);
        //System.out.println(val);
    }

    public static void test4() {
        String exp = CommandParser.parse("5*4+sqrt(pow(5,2))");
        System.out.println(exp);
        assert CommandParser.parse("5*4+sqrt(pow(5,2))").equals("5 4 * sqrt pow 5 2 +");
        Double res =  CommandParser.eval(exp);
        System.out.println(res);
        //5 4 * sqrt cos 0 25 * +
        //5 4 * sqrt pow 5 2 +
    }
}
