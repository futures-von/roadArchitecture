package com.von.base.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class MTServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(7777));
//        Worker worker = new Worker("worker-1");
        Worker[] workers = new Worker[3];
        for (int index = 0; index < workers.length; index++) {
            workers[index] = new Worker("worker-" + index);
        }
        AtomicInteger atomicInteger = new AtomicInteger();
        System.out.println(Thread.currentThread().getName() + " server is running...");
        while (true) {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    workers[atomicInteger.getAndIncrement() % workers.length].register(sc);
//                    worker.register(sc);
                }
            }
        }
    }


    static class Worker implements Runnable {
        private Selector selector;
        private String name;
        private Thread thread;
        private volatile boolean start = false;

        public Worker(String name) throws IOException {
            if (!start) {
                this.name = name;
                this.selector = Selector.open();
                this.thread = new Thread(this, name);
                this.thread.start();
            }
        }

        public void register(SocketChannel sc) throws IOException {
            sc.register(selector, SelectionKey.OP_READ);
            selector.wakeup();
            System.out.println(Thread.currentThread().getName() + " register " + sc.getRemoteAddress());
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        try {
                            if (key.isReadable()) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                try {
                                    ByteBuffer readBuffer = ByteBuffer.allocate(16);
                                    int read = clientChannel.read(readBuffer);
                                    if (read == -1) {
                                        System.out.println(Thread.currentThread().getName() + " 客户端 " + clientChannel.getRemoteAddress() + " 已下线");
                                        key.cancel();
                                        clientChannel.close();
                                        continue;
                                    }
                                    readBuffer.flip();
                                    if (readBuffer.hasRemaining()) {
                                        System.out.println(Thread.currentThread().getName() + " 服务端收到客户端" + clientChannel.getRemoteAddress() + "的数据:" + new String(readBuffer.array()));
                                    }
                                } catch (Exception e) {
                                    System.err.println(Thread.currentThread().getName() + "客户端:" + clientChannel.getRemoteAddress() + "异常" + e.getMessage());
                                    key.cancel();
                                    e.printStackTrace();
                                }
                            }
                        } finally {
                            keyIterator.remove();
                        }
                    }
                } catch (IOException e) {
                    System.err.println(Thread.currentThread().getName() + "客户端异常" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
