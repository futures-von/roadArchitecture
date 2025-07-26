package com.von.base.netty.buf;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        DefaultEventLoopGroup eventLoopGroup = new DefaultEventLoopGroup(1);
        EventLoop eventLoop = eventLoopGroup.next();
        Future<String> nettyFuture = eventLoop.submit(() -> {
            try {
                log.info("开始执行计算");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello world";
        });
        log.info("等待获取结果");
        // 异步立即获取结果，如果没有结果则返回null
//        log.info("{}", nettyFuture.getNow());
        // 异步获取结果，如果没有结果则阻塞当前线程，直到获取结果
//        log.info("{}", nettyFuture.get());
        // 异步获取结果，通过回调的方式
        nettyFuture.addListener(future -> {
            log.info("回调获取结果");
            log.info("{}", future.getNow());
        });
        eventLoopGroup.shutdownGracefully();


        ExecutorService executorService = Executors.newFixedThreadPool(1);
        java.util.concurrent.Future<String> jdkFuatre = executorService.submit(() -> {
            log.info("开始执行计算");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "hello world";
        });

        log.info("等待获取结果");
        log.info("jdk future exec result：{}", jdkFuatre.get()); // 此处会阻塞
        executorService.shutdown();
    }
}
