package alpaca.beans;

import alpaca.conf.WebProperties;
import org.junit.Test;

/**
 * Created by 腼腆的老黄 on 2017/10/11.
 */
public class AlpacaBeanInfoTest {
    @Test
    public void beanInfo() {
        AlpacaBeanInfo beanInfo =new AlpacaBeanInfo(WebProperties.class);
    }

}
