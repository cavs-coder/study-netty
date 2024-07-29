package com.study.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author fuguangwei
 * @date 2024-07-29
 */
@Slf4j
public abstract class ChannelAdapter extends Thread {

    // 用于监控通道事件的选择器
    private Selector selector;

    // 通道处理器
    private ChannelHandler channelHandler;

    // 字符集编码
    private Charset charset;

    /**
     * 构造方法，初始化选择器和字符集
     *
     * @param selector 选择器
     * @param charset  字符集编码
     */
    public ChannelAdapter(Selector selector, Charset charset) {
        this.selector = selector;
        this.charset = charset;
    }

    /**
     * 线程运行方法，不断地运行选择器以检测通道上的事件
     */
    @Override
    public void run() {
        while (true) {
            try {
                // 选择一组准备进行I/O操作的通道
                selector.select(1000);

                // 获取已选择的键集合
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                log.info("selectedKeys = {}", selectedKeys);
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;

                while (it.hasNext()) {
                    key = it.next();
                    log.info("selectedKey = {}", key);
                    // 从集合中移除当前键：确保每个 SelectionKey 在每次 Selector 调用期间只处理一次，并维护集合的正确状态和线程安全
                    it.remove();
                    // 处理输入事件
                    handleInput(key);
                }
            } catch (Exception ignore) {
                // 忽略异常
            }
        }
    }

    /**
     * 处理通道上的输入事件
     *
     * @param key 选择键，表示选择器注册的通道
     * @throws IOException 如果I/O错误发生
     */
    private void handleInput(SelectionKey key) throws IOException {
        if (!key.isValid()) return;

        // 获取通道的超类
        Class<?> superclass = key.channel().getClass().getSuperclass();

        // 处理客户端SocketChannel的连接操作
        if (superclass == SocketChannel.class) {
            SocketChannel socketChannel = (SocketChannel) key.channel();

            // 如果是连接事件（通常在客户端尝试建立连接时使用）
            if (key.isConnectable()) {
                log.info("isConnectable");
                // 完成连接
                if (socketChannel.finishConnect()) {
                    log.info("finishConnect");
                    // 初始化通道处理器
                    channelHandler = new ChannelHandler(socketChannel, charset);
                    // 触发通道激活事件
                    channelActive(channelHandler);
                    // 注册读取事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else {
                    // 如果连接失败，退出程序
                    System.exit(1);
                }
            }
        }

        // 处理服务端ServerSocketChannel的连接操作
        if (superclass == ServerSocketChannel.class) {
            // 如果是接受连接事件
            if (key.isAcceptable()) {
                log.info("isAcceptable");
                // 接受客户端连接
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                // 设置非阻塞模式
                socketChannel.configureBlocking(false);
                // 注册读取事件
                socketChannel.register(selector, SelectionKey.OP_READ);

                // 初始化通道处理器
                channelHandler = new ChannelHandler(socketChannel, charset);
                // 触发通道激活事件
                channelActive(channelHandler);
            }
        }

        // 处理读取事件(客户端或服务端的已接受连接)
        if (key.isReadable()) {
            log.info("isReadable");
            SocketChannel socketChannel = (SocketChannel) key.channel();
            // 分配了一个 1024 字节的缓冲区
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            // 从 SocketChannel 读取数据到 readBuffer 中，返回实际读取的字节数。
            int readBytes = socketChannel.read(readBuffer);
            if (readBytes > 0) {
                // 切换为读取模式
                readBuffer.flip();
                // 创建一个大小为 readBuffer.remaining() 的字节数组，这个数组大小正好等于缓冲区中剩余可以读取的数据量。
                byte[] bytes = new byte[readBuffer.remaining()];
                // 将缓冲区中的数据读到字节数组中
                readBuffer.get(bytes);
                // 触发读取消息事件
                channelRead(channelHandler, new String(bytes, charset));
            } else if (readBytes < 0) {
                // 读取到末尾，关闭通道
                key.cancel();
                socketChannel.close();
            }
        }
    }

    /**
     * 抽象方法，当通道激活时调用
     *
     * @param ctx 通道处理器上下文
     */
    public abstract void channelActive(ChannelHandler ctx);

    /**
     * 抽象方法，当读取到消息时调用
     *
     * @param ctx 通道处理器上下文
     * @param msg 读取到的消息
     */
    public abstract void channelRead(ChannelHandler ctx, Object msg);

}
