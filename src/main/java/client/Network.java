package client;

import common.Message;
import common.MessageParser;
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

    private static Network network;
    public SocketChannel socketChannel;
    private EventLoopGroup worker;
    private MessageParser callBack;

    public void doCallBack(Message par){
        callBack.method(par);
    }


    public EventLoopGroup getWorker(){
        return worker;
    }

    public static Network getInstance(){
        if(network==null){
            return new Network();
        }
        return network;
    }

    public Network() {
        network = this;
        Thread nwt = new Thread(() -> {
            worker = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(worker)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                socketChannel = ch;
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
                network = null;
            }
        });
        nwt.setDaemon(true);
        nwt.start();
    }

    public void sendMessage(Message message, MessageParser parser) {
        log.debug("Send message - {}, com.data - {}", message.getCommand(), message.getCommandData());
        socketChannel.writeAndFlush(message);
        this.callBack = parser;
    }


}
