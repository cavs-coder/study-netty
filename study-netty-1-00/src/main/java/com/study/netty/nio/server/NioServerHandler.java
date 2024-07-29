package com.study.netty.nio.server;

import com.study.netty.nio.ChannelHandler;
import com.study.netty.nio.ChannelAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.Selector;
import java.nio.charset.Charset;

/**
 * @author fuguangwei
 * @date 2024-07-29
 */
@Slf4j
public class NioServerHandler extends ChannelAdapter {

    public NioServerHandler(Selector selector, Charset charset) {
        super(selector, charset);
    }

    @Override
    public void channelActive(ChannelHandler ctx) {
        log.info("Hi, server accepted connection. socket localAddress={}", ctx.channel().socket().getLocalAddress());
        ctx.writeAndFlush("Hi, server accepted connection.");
    }

    @Override
    public void channelRead(ChannelHandler ctx, Object msg) {
        log.info("The server has received this message: {}", msg);
        ctx.writeAndFlush("The server has received this message: " + msg);
    }
}
