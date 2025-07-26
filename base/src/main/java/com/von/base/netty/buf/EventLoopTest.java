package com.von.base.netty.buf;

//import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.TimeUnit;

public class EventLoopTest {

    public static void main(String[] args) {
        // 创建 EventLoopGroup
        // 可以处理io事件，普通任务，定时任务
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
//        EventLoopGroup defaultEventExecutorGroup = new DefaultEventLoopGroup();
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());

        // 执行普通任务
        eventLoopGroup.next().execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("hello, netty");
        });

        // 执行定时任务
        eventLoopGroup.next().scheduleAtFixedRate(() -> {
            System.out.println("hello, netty");
        }, 0, 5, java.util.concurrent.TimeUnit.SECONDS);

    }
}
