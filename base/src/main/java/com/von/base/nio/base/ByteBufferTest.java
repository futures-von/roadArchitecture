package com.von.base.nio.base;

import com.von.base.util.ByteBufferUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteBufferTest {

    public static void main(String[] args) {
        String  filePath = Thread.currentThread().getContextClassLoader().getResource("data.txt").getFile();
//        readBuffer(filePath);
        readBuffer1(filePath);
    }

    /**
     * 从文件通道，使用缓冲区读取数据
     * 根据缓冲区的大小，数据没有读写完全
     * @param filePath
     */
    public static void readBuffer(String filePath) {
        // FileChannel
        //1.输入输出流， 2.RandomAccessFile
        try (FileChannel channel = new FileInputStream(filePath).getChannel()){
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            //从channel读取数据，向buffer写入
            channel.read(buffer);
            // 打印 buffer的内容
            buffer.flip();//切换至读模式
            // 是否还有剩余未读数据
            while(buffer.hasRemaining()){
                // 每次从缓冲区读取一个直接
                byte b = buffer.get();
                System.out.println((char)b);
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void readBuffer1(String filePath) {
        // FileChannel
        //1.输入输出流， 2.RandomAccessFile
        try (FileChannel channel = new FileInputStream(filePath).getChannel()){
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                //从channel读取数据，向buffer写入
                // 获取从通道读取的字节数，如果为-1表示数据读写完成
                int length = channel.read(buffer);
                if (length == -1) {
                    break;
                }
                // 打印 buffer的内容
                ByteBufferUtil.debugAll( buffer);
                //切换至读模式
                buffer.flip();
                // 是否还有剩余未读数据
                while(buffer.hasRemaining()){
                    // 每次从缓冲区读取一个直接
                    byte b = buffer.get();
                    System.out.println((char)b);
                }

                // 注意此处数据读完以后要切换为写模式，否则会导致下次读的时候数据重复
                buffer.clear();
            }

        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
