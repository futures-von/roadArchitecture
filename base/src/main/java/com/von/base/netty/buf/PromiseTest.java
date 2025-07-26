package com.von.base.netty.buf;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PromiseTest {

    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new DefaultEventLoopGroup(1);
        EventLoop executors = eventLoopGroup.next();
        DefaultPromise<String> promise = new DefaultPromise<>(executors);

        new Thread(() -> {
            try {
                log.info("begin");
                Thread.sleep(1000);
                promise.setSuccess("success");
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }
        }).start();

        promise.addListener(future -> {
            if (future.isDone()) {
                log.info("exec result: {}",future.getNow());
            }
        });

        eventLoopGroup.shutdownGracefully();
    }
}
