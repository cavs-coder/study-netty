package com.study.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author fuguangwei
 * @date 2024-08-05
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 有新的客户端连接时，netty会调用这个方法，可以在这时候对
     */
    @Override
    protected void initChannel(SocketChannel channel) {
        System.out.println("链接报告开始");
        System.out.println("链接报告信息：有一客户端链接到本服务端");
        System.out.println("链接报告IP:" + channel.remoteAddress().getHostString());
        System.out.println("链接报告Port:" + channel.remoteAddress().getPort());
        System.out.println("链接报告完毕");
    }
}
