package com.study.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

/**
 * @author fuguangwei
 * @date 2024-08-05
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //解码器，基于换行符号，客户端输入时结尾要加换行符（\n 或 \r\n）
        channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        //基于指定字符串做分隔符，等同于以上 channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        //channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,false, Delimiters.lineDelimiter()));
        //基于最大长度
        //channel.pipeline().addLast(new FixedLengthFrameDecoder(4));

        //注意调整自己的编码格式GBK（向下兼容ASCII）、UTF-8
        //将字符串编码成字节流，使得后续的处理器可以直接操作字符串，而不是字节数组
        channel.pipeline().addLast(new StringDecoder(Charset.forName("GBK")));

        //在管道中添加我们自己的接收数据实现方法
        channel.pipeline().addLast(new MyServerHandler());
    }
}
