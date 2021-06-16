package fileserver;

import client.FileCloudClient;
import common.*;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

@Slf4j
public class ServerAuthHandler extends SimpleChannelInboundHandler<Message> {

    DBService dbService;
    AuthService authService;
    User usr;

    boolean isAuth = false;

    public ServerAuthHandler(DBService dbService){
        this.dbService = dbService;
        this.authService = new AuthService(this.dbService);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message cliMsg) throws Exception {

        CommandIDs cmdID = cliMsg.getCommand();
        if(!isAuth){
            if(cmdID == CommandIDs.REQUEST_AUTHDATA){
                log.info("Try to login");
                String authStr[] = cliMsg.getCommandData().split("\\s",2);
                if(authStr.length==2){
                    try {
                        usr = authService.authUser(authStr[0], authStr[1]);
                        isAuth = true;
                        cliMsg.setCommand(CommandIDs.RESPONCE_AUTHOK);
                        ctx.writeAndFlush(cliMsg);
                        ctx.pipeline().remove(ServerAuthHandler.class);
                        ctx.pipeline().addLast(new ServerCommandHandler(dbService, authService, usr));
                    }catch(LoginFailException e){
                        log.info("Login fail");
                        cliMsg.setCommand(CommandIDs.RESPONCE_AUTHERROR);
                        cliMsg.setCommandData("Incorrect login or password");
                        ctx.writeAndFlush(cliMsg);
                    }catch (Exception e){
                        log.error("Server error: {}", e.toString());
                        cliMsg.setCommand(CommandIDs.RESPONCE_SERVERERROR);
                        cliMsg.setCommandData("Server error: " + e.toString());
                        ctx.writeAndFlush(cliMsg);
                    }
                }
            }
            else if(cmdID == CommandIDs.REQUEST_NEWUSER){

                Message msg = Message.builder()
                        .command(CommandIDs.RESPONCE_OK)
                        .build();

                try (ByteArrayInputStream bis = new ByteArrayInputStream(cliMsg.getData());
                     ObjectInputStream ois = new ObjectInputStream(bis);
                ) {
                    UserInfo userInfo = (UserInfo)ois.readObject();
                    User newUser = new User(userInfo.getLogin(), userInfo.getEmail(), "");
                    authService.registerNewUser(newUser, userInfo.getPassword());
                }catch (Exception e){
                    log.error(e.toString());
                    CommandIDs errID = CommandIDs.RESPONCE_SERVERERROR;
                    if(e instanceof LoginIsUsedException){
                        errID = CommandIDs.RESPONCE_LOGINEBUSY;
                    }else if(e instanceof EmailIsUsedException){
                        errID = CommandIDs.RESPONCE_EMALEBUSY;
                    }
                    msg.setCommand(errID);
                    msg.setCommandData(e.toString());
                }
                ctx.writeAndFlush(msg);

            }
            else if(cmdID == CommandIDs.REQUEST_CHECKNEWUSER){

                Message msg = Message.builder()
                        .command(CommandIDs.RESPONCE_OK)
                        .build();

                try (ByteArrayInputStream bis = new ByteArrayInputStream(cliMsg.getData());
                     ObjectInputStream ois = new ObjectInputStream(bis);
                ) {
                    UserInfo userInfo = (UserInfo)ois.readObject();
                    authService.checkNewUser(userInfo);
                }catch (Exception e){
                    log.error(e.toString());
                    CommandIDs errID = CommandIDs.RESPONCE_SERVERERROR;
                    if(e instanceof LoginIsUsedException){
                        errID = CommandIDs.RESPONCE_LOGINEBUSY;
                    }else if(e instanceof EmailIsUsedException){
                        errID = CommandIDs.RESPONCE_EMALEBUSY;
                    }
                    msg.setCommand(errID);
                    msg.setCommandData(e.toString());
                }
                ctx.writeAndFlush(msg);

            }
            else if(cmdID == CommandIDs.REQUEST_REMINDPASSWORD){

                Message msg = Message.builder()
                        .command(CommandIDs.RESPONCE_OK)
                        .build();

                try {
                    authService.remindPassword(cliMsg.getCommandData());
                }catch (Exception e){
                    log.error(e.toString());
                    CommandIDs errID = CommandIDs.RESPONCE_SERVERERROR;
                    if(e instanceof EmailNotFoundException){
                        errID = CommandIDs.RESPONCE_EMALEMISSING;
                    }
                    msg.setCommand(errID);
                    msg.setCommandData(e.toString());
                }
                ctx.writeAndFlush(msg);

            }
            else if(!isAuth) {
                cmdID = cliMsg.getCommand();
                if (cmdID != CommandIDs.RESPONCE_AUTHERROR) {
                    cmdID = CommandIDs.RESPONCE_ACCESSDENIED;
                    cliMsg.setCommandData("Access denied");
                }
                log.debug("Send responce");
                ctx.writeAndFlush(cliMsg);
            }
        }

    }
}
