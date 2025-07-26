package com.von.base.netty.advanced;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

public class TestLengthFieldDecoder {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                //
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 4, 4)
        , new LoggingHandler(LogLevel.DEBUG));

        channel.writeInbound(getByteBuf("hello, world", 5));
        channel.writeInbound(getByteBuf("你好！", 6));
    }

    public static ByteBuf getByteBuf(String content, int protocol) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
        buf.writeInt(content.getBytes().length);
        if (protocol > 0) {
            buf.writeInt(protocol);
        }
        buf.writeBytes(content.getBytes(Charset.defaultCharset()));
        return buf;
    }

    public static ByteBuf getByteBuf(String content) {
        return getByteBuf(content, -1);
    }
}
