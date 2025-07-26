package com.von.base.nio.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class BIOEchoServer {

    public static void main(String[] args) {
        // 使用nio来理解阻塞模式，使用单线程看是否能够满足多个客户端的通信？答案：不能，没有很好的解决方案
        // 创建一个服务器端通道
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            // 绑定指定的监听端口
            ssc.bind(new InetSocketAddress(InetAddress.getLocalHost(), 7777));
            // 创建一个客户端链接集合
            List<SocketChannel> channels = new ArrayList<>();
            System.out.println("server is running...");
            ByteBuffer buffer = ByteBuffer.allocate(16);
            // 使用无线循环来处理多个客户端
            while (true) {
                // accept用于建立客户端连接，SocketChannel主要用来与客户端通信
                // 此处会阻塞，什么是阻塞？也就是让线程停止运行，等待下一次客户端连接唤醒
                SocketChannel sc = ssc.accept(); // 阻塞方法，线程停止运行
                channels.add(sc);
                System.out.printf("%s client is connected...", sc.getRemoteAddress()).println();
                for (SocketChannel channel : channels) {
                    // 接收客户端发送的数据，此处也会阻塞
                    int readLength = channel.read(buffer); // 阻塞方法，线程停止运行
                    buffer.flip();
                    byte[] bytes = new byte[readLength];
                    while (buffer.hasRemaining()) {
                        buffer.get(bytes, 0, readLength);
                    }
                    System.out.printf("%s send message is: %s", channel.getRemoteAddress(), new String(bytes)).println();
                    buffer.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
