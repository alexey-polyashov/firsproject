package client;

import common.CloudFileSystem;
import common.Message;
import common.Options;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Network {



    public Network() {
        new Thread(() -> {
            EventLoopGroup worker = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(worker)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                FileCloudClient.socketChannel = ch;
                                ch.pipeline().addLast(
                                        new ObjectEncoder(),
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new ClientMessageHandler()
                                );
                            }
                        });
                ChannelFuture channelFuture = bootstrap.connect(Options.SERVER_HOST, Options.SERVER_PORT).sync();
                channelFuture.channel().closeFuture().sync(); // block
            } catch (Exception e) {
                log.error("e = ", e);
            } finally {
                worker.shutdownGracefully();
            }
        }).start();
    }

    public void sendMessage(Message message) {
        log.debug("Send message");
        FileCloudClient.socketChannel.writeAndFlush(message);
    }


}
