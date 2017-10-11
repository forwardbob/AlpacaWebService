package alpaca.http.servlet;

/**
 * Created by 腼腆的老黄 on 2017/9/29.
 */
public abstract class HttpServlet {
    public void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            if ("GET".equals(request.getMethod())) {
                doGet(request, response);
            } else {
                doPost(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void doGet(HttpServletRequest request, HttpServletResponse response)throws Exception;


    public abstract void doPost(HttpServletRequest request, HttpServletResponse response)throws Exception;
}
