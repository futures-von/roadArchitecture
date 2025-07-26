package com.von.base.nio.base;

import com.von.base.util.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ByteBufferToStr {
    public static void main(String[] args) {
        // 这种方式需要自己手动切换到读模式
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("hello".getBytes());
        ByteBufferUtil.debugAll(buffer);
        // bytebuffer 转 string 此次由于没有切换到读模式，所以转换数据为空
        System.out.println(StandardCharsets.UTF_8.decode(buffer));
        buffer.flip();
        System.out.println(StandardCharsets.UTF_8.decode(buffer));
        // 这种方式 默认会自动切换到读模式
        ByteBuffer byteBuffer = Charset.defaultCharset().encode("hello");
        ByteBufferUtil.debugAll(byteBuffer);
        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer));
        // 使用warp 默认会自动切换到读模式
        ByteBuffer buffer1 = ByteBuffer.wrap("hello".getBytes());
        ByteBufferUtil.debugAll(buffer1);
        System.out.println(StandardCharsets.UTF_8.decode(buffer1));




    }
}
