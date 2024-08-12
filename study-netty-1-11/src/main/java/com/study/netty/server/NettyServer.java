package com.study.netty.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fuguangwei
 * @date 2024-08-05
 */
@Slf4j
public class NettyServer {

    public static void main(String[] args) {
        new NettyServer().bind(8080);
    }

    private void bind(int port) {
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        //配置两个线程组
        b.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true) //广播
                .option(ChannelOption.SO_RCVBUF, 2048 * 1024) //设置UDP读缓冲区为2M
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024) //设置UDP写缓冲区为1M
                .handler(new MyChannelInitializer());
        try {
            //绑定端口，并启动服务器，开始接收连接
            ChannelFuture f = b.bind(port).sync();
            log.info("server start done");
            //监听通道的关闭事件
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //优雅关闭两个线程组
            group.shutdownGracefully();
        }
    }
}
