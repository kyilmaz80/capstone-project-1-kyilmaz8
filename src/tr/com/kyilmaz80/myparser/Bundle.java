package tr.com.kyilmaz80.myparser;

import java.util.ResourceBundle;

public class Bundle {
    private static ResourceBundle bundle ;

    public static ResourceBundle get() {
        return  ResourceBundle.getBundle("messages");
    }

}
