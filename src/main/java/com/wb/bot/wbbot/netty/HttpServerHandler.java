package com.wb.bot.wbbot.netty;

import com.wb.bot.wbbot.beans.ActionResult;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //获取二维码
        String qrPath = "/";
        DefaultHttpRequest request = (DefaultHttpRequest) msg;
        String uri = request.uri();
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = decoder.parameters();
        if(StringUtils.equals(uri,qrPath)){
            ctx.write(doImageResponse());
        }
        try {
            String data = ActionDispatcher.distribution(uri, parameters);
            ctx.write(doResponse(data));
        } catch (Exception e) {
            ctx.write(doResponse("方法错误"));
        }
        ctx.flush();
    }


    private FullHttpResponse doResponse(String data) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                Unpooled.wrappedBuffer(data.getBytes()));
        response.headers().set("Content-Type", "text/html; charset=GBK");
        return response;
    }

    private FullHttpResponse doImageResponse() {
        byte[] bytes = getBytes();
        if (bytes == null) {
            return doResponse("二维码为获取");
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(bytes));
        response.headers().set("Content-Type", "image/jpg");
        response.headers().set("Content-Length", response.content().readableBytes());
        response.headers().set("Connection", HttpHeaderValues.KEEP_ALIVE);
        return response;
    }

    private byte[] getBytes() {
        try {
            if (ActionResult.getInstance().getQrSrc() == null) {
                return null;
            }
            InputStream file = new FileInputStream(ActionResult.getInstance().getQrSrc());
            int size = file.available();
            byte data[] = new byte[size];
            int read = file.read(data);
            file.close();
            return data;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }


}