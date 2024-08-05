package com.study.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author fuguangwei
 * @date 2024-08-05
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        System.out.println("连接报告开始");
        System.out.println("连接报告信息：有一客户端连接到本服务端");
        System.out.println("连接报告IP:" + channel.remoteAddress().getHostString());
        System.out.println("连接报告Port:" + channel.remoteAddress().getPort());
        System.out.println("连接报告完毕");

        //在管道中添加我们自己的接收数据实现方法
        channel.pipeline().addLast(new MyServerHandler());
    }
}
