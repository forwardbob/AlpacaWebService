package alpaca.http.servlet;


import alpaca.conf.WebProperties;
import alpaca.http.session.HttpSession;
import alpaca.http.session.HttpSessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

/**
 * Created by 腼腆的老黄 on 2017/9/29.
 */
public class HttpServletRequest {
    private HttpRequest nettyRequest = null;
    private ChannelHandlerContext ctx;
    //如果更新或者生成过session 回写的时候在这获取否则一直为null
    private HttpSession session = null;

    public HttpServletRequest(HttpRequest nettyRequest, ChannelHandlerContext ctx) {
        this.nettyRequest = nettyRequest;
        this.ctx = ctx;
        //创建session
        getSession();
    }

    public HttpSession isChangeSession() {
        return session;
    }

    /**
     * 返回servlet名
     *
     * @return
     */
    public String getServletPath() {
        String[] temp = nettyRequest.getUri().split("\\?");
        return temp[0];
    }


    /**
     * 获取cookie
     *
     * @return
     */
    public Cookie[] getCookies() {
        //  Cookie[] cookies=new DefaultCookie()[];
        // ServerCookieDecoder.LAX.decode(nettyRequest.headers())
        String s = nettyRequest.headers().get(HttpHeaderNames.COOKIE);
        if (StringUtil.isNullOrEmpty(s)) {
            return null;
        }
        Set<Cookie> decode = ServerCookieDecoder.LAX.decode(s);
        Cookie[] cookies = new Cookie[decode.size()];
        decode.toArray(cookies);
        return cookies;
    }

    /**
     * 获取session
     *
     * @return
     */
    public HttpSession getSession() {
        Cookie[] cookies = getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (WebProperties.SESSION_NAME.equals(cookie.name())) {
                    //判断这个cookie中存的session是否和服务器一致
                    if (HttpSessionManager.isHasJsessionId(cookie.value())) {
                        return HttpSessionManager.getSession(cookie.value());
                    }
                    break;
                }
            }
        }
        return createSession();
    }

    /**
     * 获取请求类型
     *
     * @return
     */
    public String getMethod() {
        return nettyRequest.getMethod().name();
    }

    /**
     * 后面跟着的那个玩意儿
     *
     * @return
     */
    public String getQueryString() {
        String s = "";
        try {
            s = URLDecoder.decode(nettyRequest.uri().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            s = nettyRequest.uri().toString();
        }
        String[] temp = s.split("\\?");
        if (temp.length == 1) {
            return null;
        } else {
            return temp[1];
        }
    }

    public boolean keepAlive() {
        return isKeepAlive(nettyRequest);
    }

    /**
     * 获取参数
     *
     * @return
     */
    public Map getParameterMap() {
        Map map = new HashMap();
        if (getMethod().equals("GET")) {
            String queryString = getQueryString();
            if (StringUtil.isNullOrEmpty(queryString)) {
                return map;
            }
            String[] split = queryString.split("&");
            //判断是否存在&号
            if (split.length == 1) {
                setParam(map, queryString);
            } else {
                for (String splited : split) {
                    setParam(map, splited);
                }
            }
        } else if (getMethod().equals("POST")) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(nettyRequest);
            decoder.offer((FullHttpRequest) nettyRequest);

            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

            for (InterfaceHttpData parm : parmList) {

                Attribute data = (Attribute) parm;
                try {
                    String value = data.getValue();
                    String name = data.getName();
                    map.put(name, value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            nettyRequest.setMethod(HttpMethod.GET);
            Map parameterMap = getParameterMap();
            map.putAll(parameterMap);
        }
        return map;
    }

    private void setParam(Map map, String s) {
        String[] kv = s.split("=");
        //判断参数是否存在=号
        if (kv.length == 1) {
            map.put(kv, null);
        } else {
            map.put(kv[0], kv[1]);
        }
    }

    private HttpSession createSession() {
        String sessionId = HttpSessionManager.getSessionId();
        HttpSession session = HttpSessionManager.getSession(sessionId);
        this.session = session;
        return session;
    }

}
