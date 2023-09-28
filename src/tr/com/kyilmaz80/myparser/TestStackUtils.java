package tr.com.kyilmaz80.myparser;

import tr.com.kyilmaz80.myparser.utils.Constants;

import java.util.Stack;
import java.util.StringTokenizer;

public class TestStackUtils {
    public String expression;
    public Stack<String> stack;

    TestStackUtils(String expression) {
        this.expression = expression;
        stack = new Stack<>();
        setStack(expression);
    }

    public void setStack(String expression) {
        StringTokenizer st = new StringTokenizer(expression, Constants.DELIMITERS, true);
        while (st.hasMoreElements()) {
            String token = st.nextToken();
            if (token.equalsIgnoreCase(Constants.WHITESPACE)) {
                continue;
            }
            this.stack.push(token);
        }
    }
    public static void main(String[] args) {
        String postfix = "5 4 6";
        TestStackUtils tsu  = new TestStackUtils(postfix);

    }


}
