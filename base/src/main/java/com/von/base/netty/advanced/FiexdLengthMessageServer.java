package com.von.base.netty.advanced;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 查看粘包、拆包问题
 * 解决方法：
 * 1、采用断链接方式，及发送完成后就断开连接
 * @Author: Von
 * @Date: 2021/1/27 10:05 下午
 */
@Slf4j
public class FiexdLengthMessageServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline
                                .addLast(new FixedLengthFrameDecoder(10))
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new StringDecoder())
                                .addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                log.info("from {} client is connected", ctx.channel().remoteAddress());
                                super.channelActive(ctx);
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("from {} client receive msg: {}", ctx.channel().remoteAddress(), msg);
                                super.channelRead(ctx, msg);
                            }
                        });
                    }
                });
        // 测试黏包，调整接收缓冲区大小
        // 调整系统的接收缓冲区（滑动窗口) 全局
//        serverBootstrap.option(ChannelOption.SO_RCVBUF, 10);
        // 测试半包，调整netty发送缓冲区大小
//        serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16, 16, 16));
        ChannelFuture server = serverBootstrap.bind(7777);
        server.channel().closeFuture().sync();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
