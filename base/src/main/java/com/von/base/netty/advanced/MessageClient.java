package com.von.base.netty.advanced;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap clientBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        log.info("success connected to server : {} ", ctx.channel().remoteAddress());
                        super.channelActive(ctx);
                    }

                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {

                    }
                });
        ChannelFuture client = clientBootstrap.connect("127.0.0.1", 7777);
        client.channel().closeFuture().sync();
        workerGroup.shutdownGracefully();
    }
}
