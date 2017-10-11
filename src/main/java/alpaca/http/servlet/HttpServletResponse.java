package alpaca.http.servlet;

import alpaca.conf.WebProperties;
import alpaca.http.session.HttpSession;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static alpaca.conf.WebProperties.SESSION_NAME;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
/**
 * Created by 腼腆的老黄 on 2017/9/29.
 */
public class HttpServletResponse  {

    List<Cookie> cookies = new ArrayList();
    HttpServletRequest request;
    ChannelHandlerContext ctx;
    private HttpVersion version;
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private HttpHeaders headers;
    private Object content;
    private Charset charset = Charset.forName("UTF-8");
    private boolean keepAlive = false;
    private boolean isSend = false;

    public HttpServletResponse(HttpServletRequest request, ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.request = request;
        this.version = HttpVersion.HTTP_1_1;
        this.status = HttpResponseStatus.OK;
        this.headers = new DefaultHttpHeaders();
        this.content = Unpooled.EMPTY_BUFFER;
        this.keepAlive = request.keepAlive();
    }

    /**
     * 响应为静态资源
     *
     * @param file
     * @return
     */
    public HttpServletResponse content(File file) {
        content = file;
        return this;
    }

    /**
     * 响应内容为普通文本
     */
    public HttpServletResponse content(String str) {
        contentTypeTextPlain();
        content = Unpooled.copiedBuffer(str, charset);
        return this;
    }

    /**
     * 设置 contentType 为 text/plain
     */
    public HttpServletResponse contentTypeTextPlain() {
        headers.set(CONTENT_TYPE, "text/plain; charset=" + charset);
        return this;
    }

    /**
     * 设置 contentType 为 text/html
     */
    public HttpServletResponse contentTypeTextHtml() {
        headers.set(CONTENT_TYPE, "text/html; charset=" + charset);
        return this;
    }

    /**
     * 设置成403拒绝访问
     *
     * @param file
     */
    public void set403(File file) {
        status = HttpResponseStatus.FORBIDDEN;
        if (file == null || !file.exists() || file.isDirectory()) {
            content = Unpooled.copiedBuffer("<h1>403页面拒绝访问<h1>", charset);
            contentTypeTextHtml();
        } else {
            content(file);
        }
    }

    /**
     * 设置成404
     * @param file
     */
    public void set404(File file) {
        status = HttpResponseStatus.NOT_FOUND;
        if (file == null || !file.exists() || file.isDirectory()) {
            content = Unpooled.copiedBuffer("<h1>404页面无法找到<h1>", charset);
            contentTypeTextHtml();
        } else {
            content(file);
        }
    }

    public void setIndex(){
        File file=new File(WebProperties.STATIC_PATH+"index.html");
        if (file==null||!file.exists()){
            set404(WebProperties.FILE404);
        }else {
            content(file);
        }
    }

    public void addCookie(Cookie cookie) {
        headers.add(HttpHeaderNames.SET_COOKIE, cookie);
    }

    public void sendRedirect(String Url) {

    }

    public void sendError(int code, String msg) {

    }

    public void sendError(int code) {

    }

    public void setContentType(String str) {

    }

    public void fuck() throws Exception {
        if (isSend) {
            return;
        }
        //判断是否回写session
        HttpSession session=request.isChangeSession();
        if(session!=null){
            Cookie defaultCookie = new DefaultCookie(SESSION_NAME, session.getSessionID());
            addCookie(defaultCookie);
        }
        //判断content的类型
        if (content instanceof File) {
            File file = (File) content;
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(file, "r");
            } catch (FileNotFoundException fnfd) {
                //  sendError(ctx, HttpResponseStatus.NOT_FOUND);
                return;
            }
            long fileLength = randomAccessFile.length();
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
            response.headers().set(headers);
            response.headers().set(CONTENT_LENGTH, fileLength);
            response.headers().set(CONTENT_TYPE, parseFileMimeType(file.getName()));
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);
            ChannelFuture sendFileFuture = null;
            sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 99999));
            ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONTENT_LENGTH, response.headers().get(CONTENT_LENGTH));
            }
        } else {
            // 普通文本
            FullHttpResponse fullHttpResponse = convertFullHttpResponse();
            // 若是长连接且不需要关闭，响应头信息配置为 长连接
            if (keepAlive) {
                HttpUtil.setKeepAlive(fullHttpResponse, true);
            }
            ctx.writeAndFlush(fullHttpResponse);
        }

        isSend = true;

    }

    private FullHttpResponse convertFullHttpResponse() {
        ByteBuf tempByteBuf = (ByteBuf) content;
        headers.set(CONTENT_LENGTH, tempByteBuf.readableBytes());
        headers.set(CONTENT_ENCODING, charset);
        return new DefaultFullHttpResponse(version, status, tempByteBuf, headers, new DefaultHttpHeaders());
    }

    /**
     * 根据文件名字解析出文件类型.默认 二进制 格式
     */
    private String parseFileMimeType(String fileName) {
        String typeFor = URLConnection.getFileNameMap().getContentTypeFor(fileName);
        if (typeFor == null || typeFor.trim().length() == 0) {
            // 无法识别默认使用数据流
            if (fileName.endsWith(".css")) {
                typeFor = "text/css";
            } else if (fileName.endsWith(".js")) {
                typeFor = "application/x-javascript";
            } else
                typeFor = "application/octet-stream";
        }

        return typeFor;
    }
}
