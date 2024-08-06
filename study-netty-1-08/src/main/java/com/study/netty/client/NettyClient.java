package com.study.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fuguangwei
 * @date 2024-08-06
 */
@Slf4j
public class NettyClient {

    public static void main(String[] args) {
        // 创建 NettyClient 实例并连接到指定的主机和端口
        new NettyClient().connect("127.0.0.1", 8080);
    }

    private void connect(String inetHost, int inetPort) {
        // 创建客户端 NIO 线程组
        // 1、处理客户端所有 I/O 事件的线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 创建客户端启动辅助类，用于简化客户端的通道配置
        Bootstrap b = new Bootstrap();
        // 配置线程组
        b.group(workerGroup)
                .channel(NioSocketChannel.class) // 使用 NioSocketChannel 实例化一个新的通道对象，以建立连接
                .option(ChannelOption.AUTO_READ, true) // 配置通道选项，这里设置自动读取数据
                .handler(new MyChannelInitializer()); // 配置通道的处理器

        try {
            // 连接指定的主机和端口
            ChannelFuture f = b.connect(inetHost, inetPort).sync(); // 同步等待连接完成，确保客户端正确连接
            log.info("client start done");
            // 监听通道的关闭事件
            f.channel().closeFuture().sync(); // 同步等待通道关闭，确保客户端运行直到被关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 优雅关闭线程组
            log.info("优雅关闭线程组");
            workerGroup.shutdownGracefully();
        }
    }
}

