package com.von.base.nio.base;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class GatheringWrites {

    public static void main(String[] args) {
        String  resourcePath = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        try(FileChannel fileChannel = new RandomAccessFile(resourcePath + "gather.txt", "rw").getChannel()) {
            ByteBuffer buffer = StandardCharsets.UTF_8.encode("hello");
            ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("java");
            ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("好运连连");
            fileChannel.write(new ByteBuffer[]{buffer, buffer1, buffer2});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
