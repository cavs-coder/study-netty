package com.study.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fuguangwei
 * @date 2024-08-05
 */
@Slf4j
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 有新的客户端连接时，netty会调用这个方法，可以在这时候对
     */
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        log.info("连接报告开始");
        log.info("连接报告信息：有一客户端连接到本服务端");
        log.info("连接报告IP: {}" , channel.remoteAddress().getHostString());
        log.info("连接报告Port: {}" , channel.remoteAddress().getPort());
        log.info("连接报告完毕");
    }
}
