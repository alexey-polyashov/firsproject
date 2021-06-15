package fileserver;

import common.CloudFileSystem;
import common.CommandIDs;
import common.Message;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerAuthHandler extends SimpleChannelInboundHandler<Message> {

    DBService dbService;
    AuthService authService;
    User usr;

    boolean isAuth = false;

    public ServerAuthHandler(){
        this.dbService = new DBServiceMySQL();
        this.authService = new AuthService(this.dbService);
        this.dbService.InitDB();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        CommandIDs cmdID = msg.getCommand();
        if(!isAuth){
            if(cmdID == CommandIDs.REQUEST_AUTHDATA){
                log.info("Try to login");
                String authStr[] = msg.getCommandData().split("\\s",2);
                if(authStr.length==2){
                    try {
                        usr = authService.authUser(authStr[0], authStr[1]);
                        isAuth = true;
                        msg.setCommand(CommandIDs.RESPONCE_AUTHOK);
                        ctx.writeAndFlush(msg);
                        ctx.pipeline().remove(ServerAuthHandler.class);
                        ctx.pipeline().addLast(new ServerCommandHandler(dbService, authService, usr));
                    }catch(LoginFailException e){
                        log.info("Login fail");
                        msg.setCommand(CommandIDs.RESPONCE_AUTHERROR);
                        msg.setCommandData("Incorrect login or password");
                    }catch (Exception e){
                        log.error("Server error: {}", e.toString());
                        msg.setCommand(CommandIDs.RESPONCE_SERVERERROR);
                        msg.setCommandData("Server error: " + e.toString());
                    }
                }
            }
            if(!isAuth) {
                cmdID = msg.getCommand();
                if (cmdID != CommandIDs.RESPONCE_AUTHERROR) {
                    cmdID = CommandIDs.RESPONCE_ACCESSDENIED;
                    msg.setCommandData("Access denied");
                }
                ctx.writeAndFlush(msg);
                log.info("Send responce");
            }
        }

    }
}
