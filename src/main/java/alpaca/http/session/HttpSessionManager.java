package alpaca.http.session;

/**
 * Created by 腼腆的老黄 on 2017/9/30.
 */

import java.util.HashMap;

/**
 * HttpSession管理器
 */
public class HttpSessionManager {

    private static final HashMap<String,HttpSession> sessionMap = new HashMap<String, HttpSession>();

    private HttpSessionManager() {
    }

    /**
     * 创建一个session并返回sessionId
     * @return
     */
    public  static String getSessionId(){
        synchronized (sessionMap) {
            HttpSession httpSession = new HttpSession();
            sessionMap.put(httpSession.getSessionID(), httpSession);
            return httpSession.getSessionID();
        }
    }

    /**
     * 根据sessionId获取session信息
     * @param sessionId
     * @return
     */
    public static HttpSession getSession(String sessionId){
        return sessionMap.get(sessionId);
    }

    /**
     * 判断服务器是否包含该客户端的session信息
     * @param sessiondId
     * @return
     */
    public static boolean isHasJsessionId(String sessiondId){
        synchronized (sessionMap) {
            return sessionMap.containsKey(sessiondId);
        }
    }

}