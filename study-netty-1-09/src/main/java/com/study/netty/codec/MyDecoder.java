package com.study.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author fuguangwei
 * @date 2024-08-08
 */
public class MyDecoder extends ByteToMessageDecoder {

    //数据包最小长度：开始符号(1) + 长度(1) + 结束符号(1)
    //长度域的字节可能为0，表示数据包中没有实际内容，只有包头和包尾
    private final int BASE_LENGTH = 3;

    /**
     * 输入数据包：开始符号 + 长度 + 内容 + 结束符号
     *
     * 数据传输过程中有各种情况；整包数据、半包数据、粘包数据，比如我们设定开始符号02、结束符号03
     * 举例：
     * 整包数据（数据包是完整的）：    02 34 68 69 68 69 03
     * 半包数据（数据包是不完整的）：  02 34 68 69 68 69
     * 粘包数据（多个数据包粘在一起）：02 34 68 69 68 69 03
     * 02 34 68 69 68 69 03
     * 十六进制长度域34->4
     * 数据包实际内容：68->h、69->i
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        //基础长度不足，我们设定基础长度为3
        if (in.readableBytes() < BASE_LENGTH) {
            return;
        }

        int beginIdx; //记录包头位置

        //找到包头位置后出循环
        while (true) {
            // 获取包头开始的index
            beginIdx = in.readerIndex();
            // 标记包头开始的index
            in.markReaderIndex();
            // 读到了协议的开始标志，结束while循环
            if (in.readByte() == 0x02) {
                break;
            }
            // 未读到包头，重置读取位置为上次标记的位置
            in.resetReaderIndex();
            //为了确保找到有效的数据包起始位置，并且在处理自定义协议时，这种丢弃无效数据的操作是很常见的。这样可以确保解码器在数据包解析过程中不会因为无效数据而出错。
            in.readByte();
            // 当略过，一个字节之后，
            // 数据包的长度，又变得不满足
            // 此时，应该结束。等待后面的数据到达
            if (in.readableBytes() < BASE_LENGTH) {
                return;
            }
        }

        //剩余长度不足可读取数量[没有内容长度位]，可以等于1是长度域，只有一个长度域没用
        int readableCount = in.readableBytes();
        if (readableCount <= 1) {
            //将当前 readerIndex 重置为之前记录的 beginIdx 值，即重回包头位置
            //这个操作通常在解码过程中遇到一些错误或不完整的数据包时使用，以便重置 ByteBuf 的读取位置，让解码器可以重新尝试读取新的数据包。
            in.readerIndex(beginIdx);
            return;
        }

        //长度域占4字节，读取int
        ByteBuf byteBuf = in.readBytes(1);
        String msgLengthStr = byteBuf.toString(Charset.forName("GBK"));
        int msgLength = Integer.parseInt(msgLengthStr);

        //剩余长度不足可读取数量[没有结尾标识]，msgLength + 1是需要读取到结束的标志
        readableCount = in.readableBytes();
        if (readableCount < msgLength + 1) {
            in.readerIndex(beginIdx);
            return;
        }

        ByteBuf msgContent = in.readBytes(msgLength);

        //如果没有结尾标识，还原指针位置[其他标识结尾]
        byte end = in.readByte();
        if (end != 0x03) {
            in.readerIndex(beginIdx);
            return;
        }

        // 将读取到的内容添加到输出列表
        out.add(msgContent.toString(Charset.forName("GBK")));
    }
}
