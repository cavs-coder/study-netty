package com.study.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fuguangwei
 * @date 2024-08-09
 */
@Slf4j
public class MyOutMsgHandler2 extends ChannelOutboundHandlerAdapter {

    /**
     * 在 Channel 需要从底层读取更多数据时被调用
     */
    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        log.info("MyOutMsgHandler.read 读取了一条消息");
        //ctx.writeAndFlush("MyOutMsgHandler.read 读取了一条消息 \n");
        super.read(ctx);
    }

    /**
     * 在 Channel 需要将数据写入并发送出去时被调用
     * 时机：调用 ctx.write() 或 ctx.writeAndFlush() 时被触发，用于处理出站数据。
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("MyOutMsgHandler.write 发送了一条消息");
        //ctx.writeAndFlush("MyOutMsgHandler.write 发送了一条消息 \n");
        super.write(ctx, msg, promise);
    }
}
