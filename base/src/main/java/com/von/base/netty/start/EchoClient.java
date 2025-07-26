package com.von.base.netty.start;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;
import java.util.Scanner;

public class EchoClient {
    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
        EventLoopGroup talkGroup = new DefaultEventLoopGroup(1);
        ChannelFuture channelFuture = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
//                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new StringDecoder())
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println("收到服务端消息：" + msg);
                                        super.channelRead(ctx, msg);
                                    }
                                }).addLast(new StringEncoder())
                                .addLast(new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        System.out.println("发送数据：" + msg);
//                                ctx.channel().writeAndFlush(msg);
                                        super.write(ctx, msg, promise);
                                    }
                                });
                    }
                })
                .connect("127.0.0.1", 7777);
        channelFuture.addListener((ChannelFutureListener) future -> {
            System.out.println("连接到服务端成功");
            final Channel clientChannel = future.channel();
            talkGroup.execute(() -> {
                while (true) {
                    if (Thread.currentThread().isInterrupted()){
                        break;
                    }
                    Scanner scanner = new Scanner(System.in);
                    String text = scanner.next();
                    if ("q".equalsIgnoreCase(text)) {
                        clientChannel.close();
                        break;
                    }
                    clientChannel.writeAndFlush(text);
                }
            });
        });

        channelFuture.channel().closeFuture().addListener((ChannelFutureListener) future -> {
            System.out.printf("%s 已关闭，这是执行收尾操作", future.channel().localAddress()).println();
            talkGroup.shutdownGracefully();
            // 优雅的关闭线程
            workerGroup.shutdownGracefully();
        });
    }
}
