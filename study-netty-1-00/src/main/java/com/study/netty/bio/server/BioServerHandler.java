package com.study.netty.bio.server;

import com.study.netty.bio.ChannelAdapter;
import com.study.netty.bio.ChannelHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.nio.charset.Charset;

@Slf4j
public class BioServerHandler extends ChannelAdapter {

    public BioServerHandler(Socket socket, Charset charset) {
        super(socket, charset);
    }

    @Override
    public void channelActive(ChannelHandler ctx) {
        log.info("BioServerHandler. socket localAddress={}", ctx.getSocket().getLocalAddress());
        ctx.writeAndFlush("Hi, server accepted connection.");
    }

    @Override
    public void channelRead(ChannelHandler ctx, Object msg) {
        log.info("接收到消息：{}", msg);
        ctx.writeAndFlush("Hi, server received message: " + msg);
    }
}
