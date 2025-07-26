package com.von.base.netty.start;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class ChannelFuturaClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup workGroup = new NioEventLoopGroup(1);
        // 2.带有Future，Promise的类型都是和异步方法配套使用，用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                // 1．连接到服务器
                // 异步非阻塞，main发起了调用，真正执行connect是nio线程
                .connect("127.0.0.1", 7777);
        // 如果不调用此方法，或导致获取到的channel为null，则后续代码会报错
        // 2.1使用sycn方法同步处理结果
//        channelFuture.sync(); // 阻塞当前线程，直到nio线程链接建立完成
//        Channel channel = channelFuture.channel();
//        channel.writeAndFlush("hello, netty");
//        System.out.println("客户端启动成功");
        // 使用addListener方法异步处理结果
//        channelFuture.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                Channel channel = channelFuture.channel();
//                log.info("{} 客户端启动成功", channel.remoteAddress());
//                channel.writeAndFlush("hello, netty");
//            }
//        });
        // 如何在channel关闭之后做些收尾操作
        Channel channel = channelFuture.sync().channel();
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String text = scanner.next();
                if ("q".equalsIgnoreCase(text)) {
                    channel.close(); // 注意：这里的关闭也是异步操作
                    break;
                }
                channel.writeAndFlush(text);
            }
        }, "tallThread").start();
        // 基于同步的方式处理
//        channel.closeFuture().sync();
//        log.info("{} 已关闭，这是执行收尾操作", channel.remoteAddress());
        // 基于异步的方式处理
        channel.closeFuture().addListener((ChannelFutureListener) channelFuture1 -> {
            log.info("{} 已关闭，这是执行收尾操作", channelFuture1.channel().remoteAddress());
            // 优雅的关闭线程组
            workGroup.shutdownGracefully();
        });


    }

}
