public class TestCommandParser {
    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
        test7();
    }

    public static void test1() {
        assert CommandParser.parse("5 + 6 * 7").equals("5 6 7 * +");
        assert CommandParser.parse("5*6+7").equals("5 6 * 7 +");
        assert CommandParser.parse("5/4 *3 +2").equals("5 4 / 3 * 2 +");
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

    public static void test5() {
        assert CommandParser.eval(CommandParser.parse("5*4*cos(0)*5")) == 100.0;
        assert CommandParser.eval(CommandParser.parse("5*(4+1)+5")) == 30.0;

        //String infixExpression = "5*(4+1)+5";
        //String expectedPostfixExpression = "5 4 1 + * 5 +";

        //String infixExpression = "5+6*7";
        //String expectedPostfixExpression = "5 6 7 * +";

        String infixExpression = "5*4+sqrt(cos(0)*25)";
        String expectedPostfixExpression = "5 4 * sqrt cos 0 25 * +";
        String exp = CommandParser.parse(infixExpression);
        System.out.println(exp);
        assert CommandParser.parse(infixExpression).equals(expectedPostfixExpression);


        //assert CommandParser.eval("5 4 1 + * 5 +") == 30.0; //works
        //assert CommandParser.eval(CommandParser.parse("4+6/2*3-1")) == 12.0;
    }

    public static void test6() {
        assert CommandParser.eval(CommandParser.parse("5*(4+5)")) == 45.0;
        assert CommandParser.eval(CommandParser.parse("5*(4+sqrt(25))")) == 45.0;
        assert CommandParser.eval(CommandParser.parse("5*(4+sqrt(25)*cos(0)")) == 45.0;
        assert CommandParser.eval(CommandParser.parse("5*(4+5")) == 45.0;
        assert CommandParser.eval(CommandParser.parse("5+pow(5,2)")) == 30.0;
        assert CommandParser.eval(CommandParser.parse("5+pow(5,2)*sqrt(4)")) == 55.0;
        System.out.println();

        //5 pow 5 2 sqrt 4 * +
        //5 25 sqrt 4 * +
        //5 25 2 * +
        //5 50 +
        //55
        String expression2 = "5+pow(5,2)*sqrt(4)";
        String postfixExpression2 = CommandParser.parse(expression2);
        Double res2 = CommandParser.eval(postfixExpression2);
        System.out.println(expression2);
        System.out.println(postfixExpression2);
        System.out.println("res1: " + res2);
    }

    public static void test7() {
        assert CommandParser.eval(CommandParser.parse("5*(4+sqrt(pow(5,2)))")) == 45.0;
        //5 4 sqrt pow 5 2 + *
        String expression1 = "5*(4+sqrt(pow(5,2)))";
        //String expression = "5*(4+sqrt(25))";
        String postfixExpression1 = CommandParser.parse(expression1);
        Double res1  = CommandParser.eval(postfixExpression1);
        System.out.println(expression1);
        System.out.println(postfixExpression1);
        System.out.println("res1: " + res1);
        //assert CommandParser.eval(postfixExpression1) == 45.0;

    }



}
