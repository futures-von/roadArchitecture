package com.von.base.nio.base;

import com.von.base.util.ByteBufferUtil;

import java.nio.ByteBuffer;

public class BufferReadWrite {

    public static void main(String[] args) {
        // 缓冲三大关键属性 capacity, limit, position
        // 创建一个10字节的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(10);
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 放入数据
        buffer.put((byte) 97);
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 放入数据
        buffer.put(new byte[]{98, 99, 100});
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 切换到读模式
        buffer.flip();
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 获取数据 97
        System.out.println("1: " + buffer.get());
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 通过索引获取数据，位置指针不会移动 100
        System.out.println("2: " + buffer.get(3));
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 重置位置指针
        buffer.rewind();
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 读取数据 97
        System.out.println("3: " + buffer.get());
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 标记当前读写指针状态
        buffer.mark();
        // 获取数据 98
        System.out.println("4: " + buffer.get());
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 重置到mark标记的位置
        buffer.reset();
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 获取数据 98
        System.out.println("5: " + buffer.get());
        // 压缩缓冲区
        buffer.compact();
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);
        // 清空缓冲区，切换为写模式
        buffer.clear();
        // 输出buffer的状态
        ByteBufferUtil.debugAll(buffer);



    }
}
