package com.study.netty.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author fuguangwei
 * @date 2024-07-29
 */
public class ChannelHandler {

    // 套接字通道
    private SocketChannel channel;

    // 字符集编码
    private Charset charset;

    /**
     * 构造方法，初始化通道和字符集
     *
     * @param channel 套接字通道
     * @param charset 字符集编码
     */
    public ChannelHandler(SocketChannel channel, Charset charset) {
        this.channel = channel;
        this.charset = charset;
    }

    /**
     * 写入数据并刷新
     *
     * @param msg 要写入的消息
     */
    public void writeAndFlush(Object msg) {
        try {
            // 将消息转换为字节数组
            byte[] bytes = msg.toString().getBytes(charset);
            // 分配字节缓冲区
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            // 将字节数据放入缓冲区
            writeBuffer.put(bytes);
            // 切换缓冲区为读取模式
            writeBuffer.flip();
            // 将缓冲区数据写入通道
            channel.write(writeBuffer);
        } catch (IOException e) {
            // 捕获并抛出IO异常
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取通道
     *
     * @return 当前的SocketChannel
     */
    public SocketChannel channel() {
        return channel;
    }
}