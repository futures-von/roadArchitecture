package com.von.base.nio.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class NIOEchoServer {

    public static void main(String[] args) {
        // 使用nio来理解非阻塞模式，即使在单线程模式下也能很好的服务多个客户端的通信，
        // 但是线程不阻塞，线程会一直运行，会占用cpu资源，会很浪费cpu资源
        // 创建一个服务器端通道
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            // 绑定指定的监听端口
            ssc.bind(new InetSocketAddress(InetAddress.getLocalHost(), 7777));
            // 设置为非阻塞模式，默认是阻塞模式。
            // 非阻塞模式下accept方法不会阻塞，没有链接的情况下会返回null
            ssc.configureBlocking(false);
            // 创建一个客户端链接集合
            List<SocketChannel> channels = new ArrayList<>();
            System.out.println("server is running...");
            ByteBuffer buffer = ByteBuffer.allocate(16);
            // 使用无线循环来处理多个客户端
            while (true) {
                // accept用于建立客户端连接，SocketChannel主要用来与客户端通信
                SocketChannel sc = ssc.accept(); // 非阻塞方法，线程不会停止运行
                if (sc != null) {
                    // 设置为非阻塞模式，默认是阻塞模式
                    // 非阻塞模式下read方法不会阻塞，没有数据可读的情况下会返回-1
                    sc.configureBlocking( false);
                    channels.add(sc);
                    System.out.printf("%s client is connected...", sc.getRemoteAddress()).println();
                }

                for (SocketChannel channel : channels) {
                    // 接收客户端发送的数据，此处也会阻塞
                    int readLength = channel.read(buffer); // 非阻塞方法，线程不会停止运行
                    while (readLength > 0) {
                        buffer.flip();
                        byte[] bytes = new byte[readLength];
                        while (buffer.hasRemaining()) {
                            buffer.get(bytes, 0, readLength);
                        }
                        System.out.printf("%s send message is: %s", channel.getRemoteAddress(), new String(bytes)).println();
                        buffer.clear();
                        readLength = channel.read(buffer);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
