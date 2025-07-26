package com.von.base.nio.socket;

import com.von.base.util.BufferSplitUtil;
import com.von.base.util.ByteBufferUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectorServer {

    public static void main(String[] args) throws IOException {
        // 1.创建selector，管理多个channel
        Selector selector = Selector.open();
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.configureBlocking(false);
            // 2.建立selector和channel的联系(注册)
            // SelectionKey就是将来事件发生后，通过它可以知道事件和哪个channel的事件
            SelectionKey serverKey = ssc.register(selector, 0, null);
            // key只关注accept事件
            serverKey.interestOps(SelectionKey.OP_ACCEPT);
            ssc.bind(new InetSocketAddress(InetAddress.getLocalHost(), 7777));
            System.out.println("server is running...");
            while (true) {
                // 3.select方法，没有事件发生，线程阻塞，有事件，线程才会恢复运行
                // select事件在未处理的时候，会一直获取到该事件
                // 所以事件发生后要么处理，要么取消，不能置之不理
                selector.select();
                // 4.处理事件，selectedKeys内部包含了所有发生的事件
                // 所以要根据事件类型的不同处理不同的事件信息
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    // 如果不想处理，可以取消事件
                    // key.cancel();
                    // 5.区分事件类型，并分类处理事件
                    if (key.isAcceptable()) { // 如果是accept事件
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = channel.accept();
                        // 链接建立后，设置客户端为分阻塞
                        sc.configureBlocking(false);
                        // 6.将SocketChannel注册到selector，关注事件为可读
//                        sc.register(selector, SelectionKey.OP_READ, null);
                        // 声明一个buffer
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        // 注册附件，将buffer作为附件
                        SelectionKey clientKey = sc.register(selector, 0, buffer);
                        clientKey.interestOps(SelectionKey.OP_READ);
                        System.out.println(sc.getRemoteAddress() + " " + clientKey.hashCode() + " connected...");
                    } else if (key.isReadable()) {
                        // 如果是read事件,进行如下处理
                        // 注意：客户端不管是正常或者异常关闭后，都会引发一个read事件，
                        // 如果没做正常或者异常处理，会报错或者产生服务停止
                        SocketChannel clientChannel = null;
                        try {
                            clientChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            int readLength = clientChannel.read(buffer);
                            // 处理客户端正常关闭的链接
                            if (readLength == -1) {
                                key.cancel();
                                clientChannel.close();
                                System.out.println("客户端关闭了链接");
                            } else {
                                // 解决半包、粘包的问题
                                BufferSplitUtil.splitByCRLF(buffer);
                                if (buffer.position() == buffer.limit()) {
                                    ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                    buffer.flip();
                                    newBuffer.put(buffer);
                                    key.attach(newBuffer);
                                }
                            }
//                            while (readLength > 0) {
//                                buffer.flip();
//                                ByteBufferUtil.debugRead(buffer);
//                                byte[] bytes = new byte[readLength];
//                                while (buffer.hasRemaining()) {
//                                    buffer.get(bytes, 0, readLength);
//                                    System.out.println(new String(bytes));
//                                }
//                                buffer.clear();
//                                readLength = clientChannel.read(buffer);
//                            }
                        } catch (Exception e) {
                            // 处理客户端异常造成
                            key.cancel();
                            if (null != clientChannel) {
                                clientChannel.close();
                            }
                            e.printStackTrace();
                        }
                    }
                    // 7.处理完毕，从selectedKeys集合中删除处理完毕的事件
                    keyIterator.remove();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
