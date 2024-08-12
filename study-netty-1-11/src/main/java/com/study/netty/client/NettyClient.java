package com.study.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

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
                .channel(NioDatagramChannel.class) // 使用 NioDatagramChannel 实例化一个新的通道对象，以建立连接
                .handler(new MyChannelInitializer()); // 配置通道的处理器

        try {
            //绑定本地端口
            Channel channel = b.bind(8081).sync().channel();
            log.info("client start done");
            // 向目标端口发送信息，没有显式的 connect() 操作，数据包直接发送到指定的地址和端口
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("hello，我是客户端！", Charset.forName("GBK")), new InetSocketAddress(inetHost, inetPort))).sync();
            // 监听通道的关闭事件
            channel.closeFuture().await(); // 同步等待通道关闭，确保客户端运行直到被关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 优雅关闭线程组
            log.info("优雅关闭线程组");
            workerGroup.shutdownGracefully();
        }
    }
}

