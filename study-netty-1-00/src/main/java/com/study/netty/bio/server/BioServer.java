package com.study.netty.bio.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

@Slf4j
public class BioServer extends Thread {

    public static void main(String[] args) {
        new BioServer().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(8080));
            log.info("Bio server started on port 8080");
            while (true) {
                Socket socket = serverSocket.accept();
                BioServerHandler handler = new BioServerHandler(socket, Charset.defaultCharset());
                handler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
