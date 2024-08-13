package com.study.netty.web;

import com.study.netty.server.NettyServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author fuguangwei
 * @date 2024-08-13
 */
@RestController
@RequestMapping("/nettyServer")
public class NettyController {

    @Resource
    private NettyServer nettyServer;

    @GetMapping("/localAddress")
    public String localAddress() {
        return "nettyServer localAddress " + nettyServer.getChannel().localAddress();
    }

    @GetMapping("/isOpen")
    public String isOpen() {
        return "nettyServer isOpen " + nettyServer.getChannel().isOpen();
    }

}
