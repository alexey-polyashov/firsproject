package common;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public interface MessageParser {
    public void method(Message par, ChannelHandlerContext ctx);
}
