package com.study.netty.client;

import com.study.netty.util.MsgUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author fuguangwei
 * @date 2024-09-18
 */
@Slf4j
public class MyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当客户端主动连接服务端后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //日志信息
        SocketChannel channel = (SocketChannel) ctx.channel();
        log.info("连接报告开始");
        log.info("连接报告信息：有一客户端连接到本服务端");
        log.info("连接报告IP：{}", channel.remoteAddress().getHostString());
        log.info("连接报告Port：{}", channel.remoteAddress().getPort());
        log.info("连接报告完毕");

        //通知服务端，连接建立成功
        String str = String.format("通知服务端，连接建立成功：%s %s", new Date(), channel.localAddress().getHostString());
        ctx.writeAndFlush(MsgUtil.buildMsg(channel.id().toString(), str));
    }

    /**
     * 当客户端主动断开服务端的连接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("服务端断开连接");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("接收到消息：{}", msg);

        //通知客户端，消息发送成功
        String str = String.format("通知服务端，消息发送成功：%s，客户端接收到消息：%s", new Date(), msg);
        ctx.writeAndFlush(str);
    }

    /**
     * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭连接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.error("异常信息：{}", cause.getMessage());
    }
}
