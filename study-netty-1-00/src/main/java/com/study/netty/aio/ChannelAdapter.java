package com.study.netty.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * ChannelAdapter 类是通道适配器，用于处理异步 I/O 通道的事件。
 * 该类实现了 CompletionHandler 接口，提供了默认的事件处理逻辑。
 *
 * @author fuguangwei
 * @date 2024-07-31
 */
@Slf4j
public abstract class ChannelAdapter implements CompletionHandler<Integer, Object> {

    private AsynchronousSocketChannel channel;
    private Charset charset;

    // 构造方法，初始化通道和字符集
    public ChannelAdapter(AsynchronousSocketChannel channel, Charset charset) {
        this.channel = channel;
        this.charset = charset;
        if (channel.isOpen()) {
            channelActive(new ChannelHandler(channel, charset));
        }
    }

    // 通道激活时的抽象方法，子类必须实现
    protected abstract void channelActive(ChannelHandler ctx);

    // 通道非激活时的抽象方法，子类必须实现
    protected abstract void channelInactive(ChannelHandler ctx);

    // 读取数据时的抽象方法，子类必须实现
    protected abstract void channelRead(ChannelHandler ctx, Object msg);

    // 读取操作完成时的回调方法
    @Override
    public void completed(Integer result, Object attachment) {
        try {
            // 创建一个新的 ByteBuffer 用于读取数据
            final ByteBuffer buffer = ByteBuffer.allocate(1024);

            // 设置超时时间（秒），指定在超时之前读取操作应该完成
            final long timeout = 60 * 60L; // 1小时

            // 开始从通道读取数据，异步操作
            channel.read(buffer, timeout, TimeUnit.SECONDS, null, new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(Integer result, Object attachment) {
                    log.info("read Handler completed. result={}",result);
                    // result 是读取的字节数，当 result 为 -1 时表示通道已被关闭
                    if (result == -1) {
                        try {
                            // 调用通道非活动处理方法，执行清理工作
                            channelInactive(new ChannelHandler(channel, charset));
                            // 关闭通道，释放资源
                            channel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    // 将缓冲区从写模式切换到读模式，以读取数据
                    buffer.flip();

                    // 处理从通道读取的数据
                    channelRead(new ChannelHandler(channel, charset), charset.decode(buffer));

                    // 清空缓冲区，为下一次读取操作做准备
                    buffer.clear();

                    // 继续异步读取通道中的数据，形成循环处理
                    channel.read(buffer, timeout, TimeUnit.SECONDS, null, this);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    // 处理读取操作失败的情况
                    exc.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 读取操作失败时的回调方法
    @Override
    public void failed(Throwable exc, Object attachment) {
        exc.printStackTrace();
    }
}