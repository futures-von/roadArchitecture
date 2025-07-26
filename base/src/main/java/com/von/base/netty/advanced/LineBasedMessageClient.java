package com.von.base.netty.advanced;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Random;

@Slf4j
public class LineBasedMessageClient {

    public static String mkString(char c, int length) {
        return String.valueOf(c).repeat(Math.max(0, length)) + "\n";
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
                                            buffer.writeBytes(mkString(c++, random.nextInt(256) + 1).getBytes());
                                            ctx.channel().writeAndFlush(buffer);// 此种方式会从后往前找
                                            ctx.writeAndFlush(buffer);// 此种方式一定要注意流水线的顺序
                                        }
                                        super.channelActive(ctx);
                                    }
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("from server {} receive msg: {}", ctx.channel().remoteAddress(), msg);
                                        super.channelRead(ctx, msg);
                                    }
                                }).addLast(new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        ByteBuf buffer = (ByteBuf) msg;
                                        log.info("client write msg to server {}: {}", ctx.channel().remoteAddress(), buffer.toString(Charset.defaultCharset()));
                                        super.write(ctx, msg, promise);
                                    }
                                });
                    }
                });
        try {
            ChannelFuture client = clientBootstrap.connect("127.0.0.1", 7777).sync();
            client.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("", e);
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
