package com.study.netty.web;

import com.study.netty.server.NettyServer;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * @author fuguangwei
 * @date 2024-08-13
 */
@Slf4j
@SpringBootApplication // 标记这是一个Spring Boot应用
@ComponentScan("com.study.netty") // 扫描指定包中的Spring组件
public class NettyApplication implements CommandLineRunner {

    @Value("${netty.host}")
    private String host;

    @Value("${netty.port}")
    private int port;

    @Resource
    private NettyServer nettyServer;

    // Spring Boot应用的入口方法
    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args); // 启动Spring Boot应用
    }

    // Spring Boot启动完成后的回调方法
    @Override
    public void run(String... args) throws Exception {
        log.info("NettyApplication run success"); // 启动成功日志
        InetSocketAddress address = new InetSocketAddress(host, port); // 创建Netty服务器的地址对象
        ChannelFuture channelFuture = nettyServer.bind(address); // 绑定服务器地址并启动Netty
        Runtime.getRuntime().addShutdownHook(new Thread(() -> nettyServer.destroy())); // 添加JVM关闭钩子，在JVM关闭时优雅关闭Netty服务
        channelFuture.channel().closeFuture().syncUninterruptibly(); // 阻塞等待通道关闭
    }
}