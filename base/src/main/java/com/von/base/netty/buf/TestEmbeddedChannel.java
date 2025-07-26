package com.von.base.netty.buf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * @Description: 模拟测试socket的工作流程
 * @Author: Von
 * @CreateTime: 2020/11/23 10:05
 */
public class TestEmbeddedChannel {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("1:" + msg);
                        super.channelRead(ctx, msg);
                    }
                },
                new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("2:" + msg);
                        super.channelRead(ctx, msg);
                    }
                },
                new ChannelOutboundHandlerAdapter(){
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        System.out.println("3:" + msg);
                        super.write(ctx, msg, promise);
                    }
                },
                new ChannelOutboundHandlerAdapter(){
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        System.out.println("4:" + msg);
                        super.write(ctx, msg, promise);
                    }
                }
        );

        // 模拟入栈操作
        channel.writeInbound("hello netty");
        // 模拟出栈操作
        channel.writeOutbound("hello world");
    }
}
