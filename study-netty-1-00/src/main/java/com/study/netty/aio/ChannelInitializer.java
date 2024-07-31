package com.study.netty.aio;

import com.study.netty.aio.server.AioServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * ChannelInitializer 类用于初始化异步 I/O 通道。
 * 继承自 CompletionHandler，实现通道连接完成后的初始化逻辑。
 *
 * @author fuguangwei
 * @date 2024-07-31
 */
public abstract class ChannelInitializer implements CompletionHandler<AsynchronousSocketChannel, AioServer> {

    // 通道连接完成时的回调方法
    @Override
    public void completed(AsynchronousSocketChannel channel, AioServer attachment) {
        try {
            // 初始化通道
            initChannel(channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 接受新的客户端连接
            attachment.serverSocketChannel().accept(attachment, this);
        }
    }

    // 通道连接失败时的回调方法
    @Override
    public void failed(Throwable exc, AioServer attachment) {
        exc.printStackTrace();
    }

    // 初始化通道的抽象方法，子类必须实现
    protected abstract void initChannel(AsynchronousSocketChannel channel);
}