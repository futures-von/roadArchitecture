package com.von.base.nio.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class EchoClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(InetAddress.getLocalHost(), 7777));
        sc.write(Charset.defaultCharset().encode("hello"));
        System.out.println("client is running...");
        sc.close();
    }
}
