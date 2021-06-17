package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainServer {

    DBService dbService;
    EventLoopGroup worker;
    EventLoopGroup auth;
    ChannelFuture channelFuture;

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void stop(){
        auth.shutdownGracefully();
        worker.shutdownGracefully();
        channelFuture.channel().disconnect();
    }

    public void start() {

        dbService = new DBServiceMySQL();
        dbService.InitDB();

        auth = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ServerAuthHandler(dbService)
                            );
                        }
                    });
            channelFuture = bootstrap.bind(8189).sync();
            log.debug("Server started");
            channelFuture.channel().closeFuture().sync().channel();// block
        } catch (Exception e) {
            log.error("e=", e);
        } finally {
            log.debug("Network shutdown");
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
