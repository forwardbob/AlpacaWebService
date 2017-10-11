package alpaca;


import com.sun.org.apache.bcel.internal.util.ClassLoader;


/**
 * Created by 腼腆的老黄 on 2017/9/27.
 */
public class Startup extends ClassLoader {
    public static void main(String[] args) throws Exception {
        Alpaca alpaca = new Alpaca();
        alpaca.run(Startup.class);
    }

}
