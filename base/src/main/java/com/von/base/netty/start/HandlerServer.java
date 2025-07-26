package com.von.base.netty.start;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class HandlerServer {

    public static void main(String[] args) {
        ChannelFuture server = new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        // pipeline入栈调用顺序为从头到尾，会跳过出栈的处理器：
                        // HEAD->handler1->handler2->handler3->handler4->handler5->handler6-->TAIL
                        // pipeline出栈调用顺序为从尾到头，会跳过入栈的处理器：
                        // TAIL->handler6->handler5->handler4->handler3->handler2->handler1->HEAD
                        pipeline.addLast("handler1", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.info("handler1: {}", byteBuf.toString(Charset.defaultCharset()));
                                // 如果流水线要按照顺序执行，
                                // 则需要调用ctx.fireChannelRead 或者 super.channelRead
//                                ctx.fireChannelRead(msg);
                                super.channelRead(ctx, msg);
                            }
                        });
                        pipeline.addLast("handler2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.info("handler2: {}", byteBuf.toString(Charset.defaultCharset()));
                                super.channelRead(ctx, msg);
                            }
                        });
                        pipeline.addLast("handler3", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                String result = byteBuf.toString(Charset.defaultCharset());
                                log.info("handler3: {}", result);
                                // 注意使用 ctx.channel().writeAndFlush() 默认是从流水线的尾部往前找到下一个ChannelOutboundHandler，
                                // super.channelRead(ctx, msg); 如果后续没有入站处理器，此处就可以不用再唤醒下一个流水线了
                                ctx.channel().writeAndFlush("server say: " + result);
                                // 注意使用 ctx.writeAndFlush() 默认是从当前位置往前找到下一个ChannelOutboundHandler，
//                                ctx.writeAndFlush("server say: " + byteBuf.toString(Charset.defaultCharset()));
                            }
                        });
                        pipeline.addLast("handler4", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("handler4: {}", msg);
//                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("handler5", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("handler5: {}", msg);
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("handler6", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("handler6: {}", msg);
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                }).bind(7777);
    }
}
