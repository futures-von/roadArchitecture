package com.von.base.nio.base;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;

public class FileChannelTransferTo {

    public static void main(String[] args) {
        String  resourcePath = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        try (FileChannel from = new FileInputStream(resourcePath + "data.txt").getChannel();
             FileChannel to = new FileOutputStream(resourcePath + "to.txt").getChannel()){
            // 通道最大传输大小为2G, 超过2G会报错
//            from.transferTo(0, from.size(), to);
            // 怎么解决该问题呢？
            for (long size = from.size(), left = size; left > 0;) {
                left -= from.transferTo(size - left, left, to);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
