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

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        log.info("连接报告开始");
        log.info("连接报告信息：有一客户端连接到本服务端");
        log.info("连接报告IP: {}" , channel.remoteAddress().getHostString());
        log.info("连接报告Port: {}" , channel.remoteAddress().getPort());
        log.info("连接报告完毕");

        //在管道中添加我们自己的接收数据实现方法
        channel.pipeline().addLast(new MyServerHandler());
    }
}
