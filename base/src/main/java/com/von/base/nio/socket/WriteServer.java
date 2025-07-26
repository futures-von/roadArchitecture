package com.von.base.nio.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.bind(new InetSocketAddress(InetAddress.getLocalHost(), 7777));
            ssc.configureBlocking( false);
            SelectionKey serverKey = ssc.register(selector, 0, null);
            serverKey.interestOps(SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    try {
                        SelectionKey key = keyIterator.next();
                        if (key.isAcceptable()) {
                            SocketChannel clientChannel = ((ServerSocketChannel)key.channel()).accept();
                            System.out.println("服务端接收到新的客户端连接:" + clientChannel.getRemoteAddress());
                            clientChannel.configureBlocking(false);
                            SelectionKey selectionKey = clientChannel.register(selector, 0);
                            selectionKey.interestOps(SelectionKey.OP_READ);
                            StringBuilder stringBuilder = new StringBuilder(1000000);
                            for (int i = 0; i < 1000000; i++){
                                stringBuilder.append("a");
                            }
                            ByteBuffer buffer = Charset.defaultCharset().encode(stringBuilder.toString());
                            // 服务器一次性发送大量数据，此方法会造成服务端线程占用时间过长
                            // 造成其他客户端连接被阻塞
//                            while (buffer.hasRemaining()) {
//                                int write = clientChannel.write(buffer);
//                                System.out.println("服务端写入数据:" + write);
////                                write = clientChannel.write(buffer);
//                            }
                            // 返回实际写入的字节数
                            int write = clientChannel.write(buffer);
                            System.out.println("服务端写入数据:" + write);
                            if (buffer.hasRemaining()) {
                                // 缓存区数据未全部写入，将key再次注册为可写
                                // 基于轮询模型，可写事件发生时，将缓存区数据全部写入，并关闭通道
                                // 注册可写事件
                                selectionKey.interestOps(SelectionKey.OP_READ + SelectionKey.OP_WRITE);

                                // 将缓存区数据未写完的进行关联
                                selectionKey.attach(buffer);
                            }

                        } else if (key.isReadable()) {

                        } else if (key.isWritable()) {
                            ByteBuffer attachment = (ByteBuffer)key.attachment();
                            SocketChannel clientChannel = (SocketChannel)key.channel();
                            int write = clientChannel.write(attachment);
                            System.out.println("服务端写入数据" + write);
                            if (!attachment.hasRemaining()) {
                                // 数据全部写入，取消可写事件的关注
                                key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);
                                key.attach(null);
                            }
                        }
                    } finally {
                        keyIterator.remove();
                    }
                }

            }

        }
    }
}
