package alpaca.http.session;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by 腼腆的老黄 on 2017/9/30.
 */
public class HttpSession extends HashMap {
    public static final String JSESSIONID="JSESSIONID";

    private String sessionID= UUID.randomUUID().toString();
    //private

    public String getSessionID() {
        return sessionID;
    }

}
