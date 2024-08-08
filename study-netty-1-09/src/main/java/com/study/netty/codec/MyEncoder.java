package com.study.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * @author fuguangwei
 * @date 2024-08-08
 */
public class MyEncoder extends MessageToByteEncoder {

    /**
     * 输出数据包：长度 + 开始符号 + 内容 + 结束符号
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out) throws Exception {
        //输入的消息转字符串
        String msg = in.toString();
        //字符串转字节数组
        byte[] bytes = msg.getBytes(Charset.forName("GBK"));
        //输出的字节数组，加上开始结束符号
        byte[] send = new byte[bytes.length + 2];
        //为 send 数组赋值
        System.arraycopy(bytes, 0, send, 1, bytes.length);
        send[0] = 0x02;
        send[send.length - 1] = 0x03;

        //编码器的长度域放开头了
        out.writeInt(send.length);
        //将构建好的字节数组写入 ByteBuf
        out.writeBytes(send);
    }
}
