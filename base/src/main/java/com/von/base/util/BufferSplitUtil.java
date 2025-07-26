package com.von.base.util;

import java.nio.ByteBuffer;

import static com.von.base.util.ByteBufferUtil.debugAll;

public class BufferSplitUtil {

    /**
     * 按照分割符进行消息的拆分，并返回新的ByteBuffer
     * @param source 源ByteBuffer
     * @param delim 自定义分割符
     */
    public static void split(ByteBuffer source, char delim) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            //找到一条完整消息
            if (source.get(i) == delim) {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }

    public static void splitByCRLF(ByteBuffer source) {
        split(source, '\n');
    }

}
