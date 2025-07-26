package com.von.base.netty.start;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServer {

    public static void main(String[] args) {
        // 创建一个服务启动器
        new ServerBootstrap()
                // 添加一个事件循环处理组
                .group(new NioEventLoopGroup(2))
                // 指定服务端通道实现为NioServerSocketChannel
                .channel(NioServerSocketChannel.class)
                // 添加一个通道初始化器，用于处理服务端通道（Channel）
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nsc) throws Exception {
                        // 添加一个StringDecoder，用于将字节解码为字符串
                        nsc.pipeline().addLast(new StringDecoder());
                        // 添加一个ChannelInboundHandlerAdapter，用于处理接收到的消息
                        nsc.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("receive message: {}", msg);
                            }
                        });
                    }
                })
                .bind(7777); // 绑定端口号
    }
}
