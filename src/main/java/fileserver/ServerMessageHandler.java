package fileserver;

import common.CloudFileSystem;
import common.CommandIDs;
import common.Message;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ServerMessageHandler extends SimpleChannelInboundHandler<Message> {

    DBService dbService;
    AuthService authService;
    User usr;

    boolean isAuth = false;
    CloudFileSystem fs;

    public ServerMessageHandler(){
        this.dbService = new DBServiceMySQL();
        this.authService = new AuthService(this.dbService);
        this.dbService.InitDB();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        if(!isAuth){
            if(msg.command == CommandIDs.REQUEST_AUTHDATA){
                log.info("Try to login");
                String authStr[] = msg.commandData.split("\\s",2);
                if(authStr.length==2){
                    try {
                        usr = authService.authUser(authStr[0], authStr[1]);
                        isAuth = true;
                        msg.command = CommandIDs.RESPONCE_AUTHOK;
                        ctx.writeAndFlush(msg);
                    }catch(LoginFailException e){
                        log.info("Login fail");
                        msg.command = CommandIDs.RESPONCE_AUTHERROR;
                        msg.commandData = "Incorrect login or password";
                    }
                }
            }
            if(!isAuth){
                if(msg.command != CommandIDs.RESPONCE_AUTHERROR) {
                    msg.command = CommandIDs.RESPONCE_CONNECTIONERROR;
                    msg.commandData = "Request is not available";
                }
                ChannelFuture f = ctx.writeAndFlush(msg);
                log.info("Send responce");
                f.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        assert f == future;
                        log.info("Close channel");
                        ctx.close();
                    }
                });
            }
        }else{
            log.info("Receive command");
            SrvCommandManager.invokeCommand(msg, fs);
        }

    }
}
