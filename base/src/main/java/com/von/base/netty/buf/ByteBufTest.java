package com.von.base.netty.buf;

import com.von.base.util.ByteBufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

public class ByteBufTest {

    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(10);
        byteBuf.writeByte(6);
        byteBuf.writeInt(6);

        ByteBufferUtil.debugBuf(byteBuf);
//        log.info(""+byteBuf);
        byteBuf.writeBytes(new byte[]{1,2,3,4});
        ByteBufferUtil.debugBuf(byteBuf);
        byteBuf.writeBytes(new byte[]{1,2,3,4});
        ByteBufferUtil.debugBuf(byteBuf);
        // 创建切片，表示从那个索引开始，截取的长度
        // 创建的切片会影响原ByteBuf，改变切片，原ByteBuf也会改变
        // 由于是零拷贝，所以使用要注意
        // 另外切片后的byte不允许扩容，会抛异常
//        byteBuf.retain();
        ByteBuf slice1 = byteBuf.slice(0, 5);
        slice1.retain();
        ByteBuf slice2 = byteBuf.slice(5, 5);
        slice2.retain();
        ByteBufferUtil.debugBuf(slice1);
        ByteBufferUtil.debugBuf(slice2);
        // 如果ByteBuf调用了release方法，那么也会影响切片的ByteBuf
        // 所以在使用切片的时候，可以调用ByteBuf的retain方法，来增加ByteBuf的引用计数
        slice1.setByte(1, 77);
        ByteBufferUtil.debugBuf(slice1);
        // 可以看到切片的ByteBuf改变了，源ByteBuf也改变了
        ByteBufferUtil.debugBuf(byteBuf);
        byteBuf.release();
        // 由于byteBuf已经回收，此处执行会抛异常
        // io.netty.util.IllegalReferenceCountException: refCnt: 0
        // 所以在执行slice2.setByte(1,88);之前，请先执行byteBuf.retain方法，表示手动引用
        slice2.setByte(1,88);
    }


}
