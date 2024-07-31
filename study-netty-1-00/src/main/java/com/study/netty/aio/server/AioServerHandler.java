package com.study.netty.aio.server;

import com.study.netty.aio.ChannelAdapter;
import com.study.netty.aio.ChannelHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;

/**
 * AioServerHandler 类处理服务器端的 I/O 事件。
 * 继承自 ChannelAdapter，实现了对通道事件的处理逻辑。
 *
 * @author fuguangwei
 * @date 2024-07-31
 */
@Slf4j
public class AioServerHandler extends ChannelAdapter {

    // 构造方法，初始化异步套接字通道和字符集
    public AioServerHandler(AsynchronousSocketChannel channel, Charset charset) {
        super(channel, charset);
    }

    // 通道激活时的处理逻辑
    @Override
    protected void channelActive(ChannelHandler ctx) {
        try {
            log.info("Hi, server accepted connection. socket localAddress={}", ctx.channel().getRemoteAddress());
            ctx.writeAndFlush("Hi, server accepted connection.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 通道非激活时的处理逻辑
    @Override
    protected void channelInactive(ChannelHandler ctx) {
        log.info("Hi, channelInactive");
    }

    // 读取数据时的处理逻辑
    @Override
    protected void channelRead(ChannelHandler ctx, Object msg) {
        log.info("The server has received this message: {}", msg);
        ctx.writeAndFlush("The server has received this message: " + msg);
    }
}