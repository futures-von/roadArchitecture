package com.von.base.netty.buf;

import com.von.base.util.ByteBufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

public class CompositeByteBufTest {

    public static void main(String[] args) {
        // 将多个小的ByteBuf组合成一个大的ByteBuf，也是一个零拷贝操作
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        // 传统方式
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(buf1).writeBytes(buf2);
        ByteBufferUtil.debugBuf(byteBuf);

        // 组合方式
        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
//        compositeByteBuf.addComponents(buf1, buf2, byteBuf); // 不会
        compositeByteBuf.addComponents(true, buf1, buf2, byteBuf);

    }
}
