package com.wb.bot.wbbot.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

public class HttpChannelInitService extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel sc) {
        sc.pipeline().addLast(new HttpResponseEncoder());

        sc.pipeline().addLast(new HttpRequestDecoder());


        sc.pipeline().addLast(new HttpServerHandler());
    }

}
