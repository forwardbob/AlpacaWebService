package alpaca.conf;

import java.io.File;

/**
 * Created by 腼腆的老黄 on 2017/10/9.
 */
public class WebProperties {
    public static Integer PORT=8844;
    public static String SESSION_NAME="AlpacaSession";
    public static String WEB_PATH="/static";
    public static String BASE_PATH=System.getProperty("user.dir");
    public static String STATIC_PATH=BASE_PATH+"/"+WEB_PATH+"/";
    public static File FILE403=null;
    public static File FILE404=null;
}
