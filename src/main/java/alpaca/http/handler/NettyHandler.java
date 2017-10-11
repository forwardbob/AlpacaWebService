package alpaca.http.handler;

import alpaca.http.servlet.HttpServlet;
import alpaca.http.servlet.HttpServletRequest;
import alpaca.http.servlet.HttpServletResponse;
import alpaca.http.template.StaticServlet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

import java.io.File;

import static alpaca.conf.WebProperties.FILE404;
import static alpaca.conf.WebProperties.STATIC_PATH;

/**
 * Created by 腼腆的老黄 on 2017/10/1.
 */
public class NettyHandler extends ChannelInboundHandlerAdapter {
    HttpRequest request;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            request = (FullHttpRequest) msg;
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            HttpServletRequest httpServletRequest = new HttpServletRequest(request, ctx);
            HttpServletResponse httpServletResponse = new HttpServletResponse(httpServletRequest, ctx);
            boolean isMatch = false;
            String rm = STATIC_PATH + httpServletRequest.getServletPath();
            try {
                File files = new File(rm);
                if (files.exists()) {
                    //调用静态资源servlet
                    HttpServlet staticServlet = new StaticServlet();
                    staticServlet.service(httpServletRequest, httpServletResponse);
                    isMatch = true;
                } else {
                    //调用配置文件中的servlet
                    //find Servlet
                    // 修改isMatch的标志
                    //404是否交给ServletHandler
                }
                //还是没匹配到就404
                if (isMatch == false) {
                    httpServletResponse.set404(FILE404);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpServletResponse.fuck();
            }
        }
    }


}
