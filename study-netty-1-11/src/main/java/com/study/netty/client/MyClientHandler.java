package com.study.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @author fuguangwei
 * @date 2024-08-12
 */
@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    /**
     * 接收服务端发送的内容
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) throws Exception {
        String msg = packet.content().toString(Charset.forName("GBK"));
        log.info("UDP客户端接收到消息：{}", msg);
    }

}
