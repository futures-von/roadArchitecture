package com.von.base.nio.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WriteClient {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 7777));

        int total = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
        while (true) {
            int read = socketChannel.read(byteBuffer);
            if (read == -1) {
                break;
            }
            byteBuffer.flip();
            byteBuffer.clear();
            total += read;
            System.out.println(total);
        }

        System.out.println("total:" + total);
    }
}
