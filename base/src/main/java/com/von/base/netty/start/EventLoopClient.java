package com.von.base.netty.start;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class EventLoopClient {

    public static void main(String[] args) throws InterruptedException {

        // 创建客户端启动器
        Channel channel = new Bootstrap()
                // 添加一个事件循环处理组
                .group(new NioEventLoopGroup(1))
                // 指定客户端通道实现为NioSocketChannel
                .channel(NioSocketChannel.class)
                // 添加处理器，自定一个通道初始化器，用于处理客户端通道（Channel）
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                // 绑定服务器的IP和端口
                .connect("127.0.0.1", 7777)
                // 阻塞方法，直到链接建立
                .sync()
                // 获取通道
                .channel();
        channel.writeAndFlush("hello, netty");
        System.out.println("客户端启动成功");
    }

}
