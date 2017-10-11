package alpaca;

import alpaca.beans.AlpacaBeanInfo;
import org.junit.Test;

/**
 * Created by 腼腆的老黄 on 2017/9/27.
 */

public class AlpacaTest {
    @Test
    public void run() throws Exception {
      //  AlpacaBeanInfo alpacaBeanInfo=new AlpacaBeanInfo(ServerConfig.class);

    }

    @org.junit.Test
    public void assembleWebConfig() throws Exception {
        Alpaca alpaca=new Alpaca();
    }

    public static void main(String[] args) {
        Alpaca alpaca=new Alpaca();

     //   AlpacaBeanInfo alpacaBeanInfo=new AlpacaBeanInfo(ServerConfig.class);
       // alpaca.setBeanValue(new AlpacaBeanInfo(ServerConfig.class),"port",0);
    }


}
