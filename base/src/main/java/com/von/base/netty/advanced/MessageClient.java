package com.von.base.netty.advanced;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class MessageClient {

    public static void sendMsg() {
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap clientBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new StringDecoder())
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        log.info("success connected to server : {} ", ctx.channel().remoteAddress());
//                                        for (int i = 0; i < 10; i++) {
                                        ByteBuf buffer = ctx.alloc().buffer(16);
                                        buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17});
                                        ctx.writeAndFlush(buffer);
//                                        }
//                                        super.channelActive(ctx);
                                        ctx.channel().close();
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("from server {} receive msg: {}", ctx.channel().remoteAddress(), msg);
                                        super.channelRead(ctx, msg);
                                    }
                                }).addLast(new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.info("client write msg to server {}: {}", ctx.channel().remoteAddress(), msg);
                                        super.write(ctx, msg, promise);
                                    }
                                });
                    }
                });
        try {
            ChannelFuture client = clientBootstrap.connect("127.0.0.1", 7777).sync();
            client.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            sendMsg();
            log.info("第{}次发送消息", i);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
