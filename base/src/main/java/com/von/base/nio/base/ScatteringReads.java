package com.von.base.nio.base;

import com.von.base.util.ByteBufferUtil;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ScatteringReads {
    public static void main(String[] args) {
        String  resourcePath = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        try(FileChannel fileChannel = new RandomAccessFile(resourcePath + "data.txt", "r").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(10);
            ByteBuffer buffer1 = ByteBuffer.allocate(26);
            ByteBuffer buffer2 = ByteBuffer.allocate(3);
            fileChannel.read(new ByteBuffer[]{
                    buffer, buffer1, buffer2
            });
            ByteBufferUtil.debugAll(buffer);
            ByteBufferUtil.debugAll(buffer1);
            ByteBufferUtil.debugAll(buffer2);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
