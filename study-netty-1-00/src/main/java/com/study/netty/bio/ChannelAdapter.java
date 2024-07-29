package com.study.netty.bio;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;

@Slf4j
public abstract class ChannelAdapter extends Thread {

    private Socket socket;
    private Charset charset;
    private ChannelHandler channelHandler;

    public ChannelAdapter(Socket socket, Charset charset) {
        this.socket = socket;
        this.charset = charset;
        while (!socket.isConnected()) {
            break;
        }
        channelHandler = new ChannelHandler(socket, charset);
        channelActive(channelHandler);
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), charset));
            String line = null;
            while ((line = input.readLine()) != null) {
                channelRead(channelHandler, line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void channelActive(ChannelHandler ctx);

    public abstract void channelRead(ChannelHandler ctx, Object msg);
}
