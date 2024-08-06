package com.study.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fuguangwei
 * @date 2024-08-06
 */
@Slf4j
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        log.info("连接报告开始");
        log.info("链接报告信息：本客户端链接到服务端。channelId：{}", channel.id());
        log.info("连接报告完毕");
    }
}
