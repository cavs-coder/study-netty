package com.study.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @author fuguangwei
 * @date 2024-08-06
 */
@Slf4j
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //解码器，基于换行符号，客户端输入时结尾要加换行符（\n 或 \r\n）
        channel.pipeline().addLast(new LineBasedFrameDecoder(1024));

        //注意调整自己的编码格式GBK（向下兼容ASCII）、UTF-8
        //将字符串编码成字节流，使得后续的处理器可以直接操作字符串，而不是字节数组
        channel.pipeline().addLast(new StringDecoder(Charset.forName("GBK")));
        //将字符串编码成字节流，字符串编码器将客户端发送的消息编码为字节流，以便通过网络传输
        channel.pipeline().addLast(new StringEncoder(Charset.forName("GBK")));

        //在管道中添加我们自己的接收数据实现方法
        channel.pipeline().addLast(new MyClientHandler());
    }
}
