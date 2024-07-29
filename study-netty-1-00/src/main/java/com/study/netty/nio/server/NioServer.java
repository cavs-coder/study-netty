package com.study.netty.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;

/**
 * @author fuguangwei
 * @date 2024-07-29
 */
public class NioServer {

    // 用于监控通道上事件（如连接请求）的选择器
    private Selector selector;

    // 用于监听传入连接的ServerSocketChannel
    private ServerSocketChannel serverSocketChannel;

    // 主方法，用于启动服务器
    public static void main(String[] args) {
        // 创建一个NioServer实例，并绑定到8080端口
        new NioServer().bind(8080);
    }

    // 将服务器绑定到指定端口的方法
    private void bind(int port) {
        try {
            // 打开一个选择器
            selector = Selector.open();
            // 打开一个ServerSocketChannel
            serverSocketChannel = ServerSocketChannel.open();
            // 配置通道为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            // 绑定端口并设置连接请求的最大排队数量为1024
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            // 将通道注册到选择器上，并关注接受连接事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            new NioServerHandler(selector, Charset.defaultCharset()).start();
        } catch (IOException e) {
            // 捕获并抛出IO异常
            throw new RuntimeException(e);
        }
    }
}

