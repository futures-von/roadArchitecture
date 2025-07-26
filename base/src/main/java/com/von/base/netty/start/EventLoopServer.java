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
public class EventLoopServer {
    public static void main(String[] args) {
        // 创建一个独立的 EventLoopGroup
        EventLoopGroup eventLoopGroup = new DefaultEventLoopGroup();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);
        ChannelFuture channelFuture = new ServerBootstrap()
                // boss 和 worker
                // 细分1:boss 只负责 ServerSocketChannel上accept 事件, worker只负责socketChannel上的读写
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf byteBuf = (ByteBuf) msg;
                                        log.info("msg: " + byteBuf.toString(Charset.defaultCharset()));
                                        ctx.fireChannelRead(byteBuf);
                                    }
                                })
                                .addLast(eventLoopGroup, new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        super.channelRead(ctx, msg);
                                        ByteBuf byteBuf = (ByteBuf) msg;
                                        log.info("msg: " + byteBuf.toString(Charset.defaultCharset()));
                                    }
                                });
                    }
                })
                .bind(7777);
        // 服务端执行收尾操作
        channelFuture.channel()
                .closeFuture()
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        workerGroup.shutdownGracefully();
                        bossGroup.shutdownGracefully();
                        log.info("{} 已关闭，这是执行收尾操作", channelFuture.channel().remoteAddress());
                    }
                });
    }
}
