package com.study.netty.aio.server;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * AioServer 类是一个异步 I/O 服务器的实现。
 * 它继承自 Thread 类，通过 AsynchronousServerSocketChannel 实现异步 I/O 操作。
 *
 * @author fuguangwei
 * @date 2024-07-31
 */
@Slf4j
public class AioServer extends Thread {

    // 异步服务器套接字通道
    private AsynchronousServerSocketChannel serverSocketChannel;

    // 获取异步服务器套接字通道
    public AsynchronousServerSocketChannel serverSocketChannel() {
        return serverSocketChannel;
    }

    // 主方法，启动服务器
    public static void main(String[] args) {
        new AioServer().start();
    }

    // 线程运行方法
    @Override
    public void run() {
        try {
            // 创建一个 AsynchronousServerSocketChannel 实例，并绑定到指定端口
            serverSocketChannel = AsynchronousServerSocketChannel.open(AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 10));
            serverSocketChannel.bind(new InetSocketAddress(8080));

            // 使用 CountDownLatch 保持主线程存活
            CountDownLatch latch = new CountDownLatch(1);

            //1、accept 方法是一个非阻塞的异步操作，立即返回，而不是等待客户端连接
            //2、异步接受一个新的客户端连接，成功建立连接后，会调用回调处理器 acceptHandler 的 completed 方法，处理初始化通道等
            AioServerChannelInitializer acceptHandler = new AioServerChannelInitializer();
            serverSocketChannel.accept(this, acceptHandler);

            // 等待，直到 CountDownLatch 被倒计时到零
            latch.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}