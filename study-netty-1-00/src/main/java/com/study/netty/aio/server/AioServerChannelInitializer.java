package com.study.netty.aio.server;

import com.study.netty.aio.ChannelInitializer;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * AioServerChannelInitializer 类用于初始化异步 I/O 通道。
 * 继承自 ChannelInitializer，负责设置通道的初始状态。
 *
 * @author fuguangwei
 * @date 2024-07-31
 */
@Slf4j
public class AioServerChannelInitializer extends ChannelInitializer {

    // 初始化通道的方法
    @Override
    protected void initChannel(AsynchronousSocketChannel channel) {
        log.info("initChannel");

        // 分配一个容量为 1024 字节的缓冲区，用于存储从通道中读取的数据
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 设置读取操作的超时时间为 60 秒，时间单位为秒
        long timeout = 60;
        TimeUnit unit = TimeUnit.SECONDS;

        // 创建 AioServerHandler 实例，作为回调处理器
        // 回调处理器会在读取操作完成时处理数据
        AioServerHandler handler = new AioServerHandler(channel, Charset.defaultCharset());

        // 开始从通道中读取数据，指定缓冲区、超时时间和回调处理器
        channel.read(buffer, timeout, unit, null, handler);
    }
}
