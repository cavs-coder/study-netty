package com.study.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fuguangwei
 * @date 2024-09-18
 */
@Slf4j
public class NettyServer {

    public static void main(String[] args) {
        new NettyServer().bind(8080);
    }

    private void bind(int port) {
        //配置服务端NIO线程组
        //1、处理客户端连接请求的线程组，也叫 boss group
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        //2、处理已接受连接的I/O操作（读写操作）的线程组，也叫 worker group
        EventLoopGroup childGroup = new NioEventLoopGroup();

        //创建服务端启动辅助类，用于简化服务端的通道配置
        ServerBootstrap b = new ServerBootstrap();
        //配置两个线程组
        b.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)//使用NioServerSocketChannel实例化一个新的通道对象，以接收传入的连接
                .option(ChannelOption.SO_BACKLOG, 128)//配置通道选项，这里设置的时服务端接收连接的队列长度
                .childHandler(new MyChannelInitializer());//配置子通道的处理器
        try {
            //绑定端口，并启动服务器，开始接收连接
            ChannelFuture f = b.bind(port).sync();//同步等待绑定完成，确保服务器正确启动
            log.info("server start done");
            //监听通道的关闭事件
            f.channel().closeFuture().sync();//同步等待通道关闭，确保服务器运行直到被关闭。
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //优雅关闭两个线程组
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
    }
}
