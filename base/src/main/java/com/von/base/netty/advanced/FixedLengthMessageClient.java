package com.von.base.netty.advanced;

import com.von.base.util.ByteBufferUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FixedLengthMessageClient {

    public static byte[] fillByte(char c, int length) {
        byte[] bytes = new byte[10];
        for (int index = 0; index < 10; index++) {
            if (index < length) {
                bytes[index] = (byte) c;
            } else {
                bytes[index] = (byte) '_';
            }
        }
//        Arrays.fill(bytes, (byte) c);
        return bytes;
    }
    public static void sendMsg() {
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap clientBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new StringDecoder())
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        log.info("success connected to server : {} ", ctx.channel().remoteAddress());
                                        char c = 'a';
                                        Random random = new Random();
                                        for (int i = 0; i < 10; i++) {
                                            ByteBuf buffer = ctx.alloc().buffer(10);
                                            buffer.writeBytes(fillByte(c++, random.nextInt(10) + 1));
                                            ctx.writeAndFlush(buffer);
                                        }
//                                        super.channelActive(ctx);
//                                        ctx.channel().close();
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
//        for (int i = 0; i < 10; i++) {
            sendMsg();
//            log.info("第{}次发送消息", i);
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

//        byte[] bytes = fillByte('c', 6);
//        System.out.println(Arrays.toString(bytes));
//        System.out.println(ByteBufferUtil.debugBuf());
    }
}
