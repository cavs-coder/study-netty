package com.study.netty.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;

/**
 * ChannelHandler 类是通道处理器，封装了通道和字符集的操作。
 *
 * @author fuguangwei
 * @date 2024-07-31
 */
public class ChannelHandler {

    private AsynchronousSocketChannel channel;
    private Charset charset;

    // 构造方法，初始化通道和字符集
    public ChannelHandler(AsynchronousSocketChannel channel, Charset charset) {
        this.channel = channel;
        this.charset = charset;
    }

    // 获取通道
    public AsynchronousSocketChannel channel() {
        return channel;
    }

    /**
     * 写入数据并刷新
     *
     * @param msg 要写入的消息
     */
    public void writeAndFlush(Object msg) {
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
    }
}