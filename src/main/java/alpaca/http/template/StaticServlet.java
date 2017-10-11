package alpaca.http.template;

import alpaca.conf.WebProperties;
import alpaca.http.servlet.HttpServlet;
import alpaca.http.servlet.HttpServletRequest;
import alpaca.http.servlet.HttpServletResponse;

import java.io.File;

import static alpaca.conf.WebProperties.FILE403;
import static alpaca.conf.WebProperties.FILE404;

/**
 * Created by 腼腆的老黄 on 2017/10/11.
 */
public class StaticServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String rm = "";
        //判断是不是首页
        if (request.getServletPath().equals("/")) {
            response.setIndex();
            return;
        } else {
            rm = WebProperties.STATIC_PATH + request.getServletPath();
        }
        File file = new File(rm);

        //判断是否存在,路径
        if (file.isDirectory()) {
            response.set403(FILE403);
        } else if (!file.exists()) {
            response.set404(FILE404);
        } else {
            response.content(file);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

    }

}
