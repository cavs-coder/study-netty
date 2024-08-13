package com.study.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author fuguangwei
 * @date 2024-08-13
 */
@Slf4j
@Component
public class NettyServer {

    // 配置服务端线程组，用于处理网络事件
    private final EventLoopGroup parentGroup = new NioEventLoopGroup(); // 用于处理连接的接受
    private final EventLoopGroup childGroup = new NioEventLoopGroup();  // 用于处理实际的读写操作
    private Channel channel; // 定义一个通道对象，用于绑定和管理服务器的通道

    // 绑定服务端口
    public ChannelFuture bind(InetSocketAddress address) {
        ChannelFuture channelFuture = null;
        try {
            ServerBootstrap b = new ServerBootstrap(); // 创建服务端引导类
            b.group(parentGroup, childGroup) // 设置线程组
                    .channel(NioServerSocketChannel.class) // 指定使用NIO传输类型的通道
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置服务端接受连接的队列大小
                    .childHandler(new MyChannelInitializer()); // 设置子处理器，处理请求通道上的事件
            channelFuture = b.bind(address).syncUninterruptibly(); // 绑定端口并同步等待完成
            channel = channelFuture.channel(); // 获取绑定成功的通道
        } catch (Exception e) {
            throw new RuntimeException(e); // 捕获异常并抛出运行时异常
        } finally {
            if (channelFuture != null && channelFuture.isSuccess()) {
                log.info("server start done"); // 服务启动成功日志
            } else {
                log.error("server start error"); // 服务启动失败日志
            }
        }
        return channelFuture; // 返回ChannelFuture对象，用于后续操作
    }

    // 销毁Netty服务端
    public void destroy() {
        if (channel == null) {
            return; // 如果通道为空，则直接返回
        }
        channel.close(); // 关闭通道
        parentGroup.shutdownGracefully(); // 优雅地关闭主线程组
        childGroup.shutdownGracefully(); // 优雅地关闭子线程组
    }

    // 获取通道对象
    public Channel getChannel() {
        return channel;
    }
}
