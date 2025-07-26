package com.von.base.bio;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocketFactory serverSocketFactory = ServerSocketFactory.getDefault();
        ServerSocket serverSocket = serverSocketFactory.createServerSocket(8888);
        while (true) {
            try {
                Socket accept = serverSocket.accept();
                SocketChannel channel = accept.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                int read = channel.read(byteBuffer);
                while (read != -1) {
                    byteBuffer.flip();
                    while (byteBuffer.hasRemaining()) {
                        System.out.print((char) byteBuffer.get());
                    }
                    byteBuffer.clear();
                    read = channel.read(byteBuffer);
                }
                System.out.println("连接成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
