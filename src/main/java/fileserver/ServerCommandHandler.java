package fileserver;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import common.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class ServerCommandHandler  extends SimpleChannelInboundHandler<Message> {

    private DBService dbService;
    private AuthService authService;
    private User usr;

    CloudFileSystem fs;

    public ServerCommandHandler(DBService dbService, AuthService authService, User usr){
        this.dbService = dbService;
        this.authService = authService;
        this.usr = usr;
        fs = new CloudFileSystem(Paths.get(Options.SERVER_ROOT, usr.getFolder()), Paths.get(usr.getFolder()), true);
        if(!fs.pathExists(fs.getUserFolder())){
            fs.makeDir(fs.getUserFolder());
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        CommandIDs cmdID = msg.getCommand();
        log.info("Receive command {}",cmdID);

        if(cmdID == CommandIDs.REQUEST_FILELIST){
            List<FileInfo> fl = fs.getFullList();
            Message mes = Message.builder()
                    .command(CommandIDs.RESPONCE_FILELIST)
                    .build();
            try(
                    ByteOutputStream bos = new ByteOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    ){
                oos.writeObject(fl);
                mes.setData(bos.getBytes());
                ctx.writeAndFlush(mes);
            }catch (Exception e){
                log.error("Server error - {}", e.toString());
                mes = Message.builder()
                        .command(CommandIDs.RESPONCE_SERVERERROR)
                        .commandData(e.toString())
                        .build();
                ctx.writeAndFlush(mes);
            }
        }
        else if(cmdID == CommandIDs.REQUEST_SENDFILE){
            Path fName = fs.getAbsolutePathToFile(msg.getCommandData());
            log.debug("Try to write in file {}", fName);
            try {
                Message mes = fs.putFilePart(fName, msg);
                ctx.writeAndFlush(mes);
            }catch(Exception e){
                log.error("Save file error: " + e.toString());
                fs.resetChannel();
                Message mes = Message.builder()
                        .command(CommandIDs.RESPONCE_SERVERERROR)
                        .commandData(e.toString())
                        .build();
                ctx.writeAndFlush(mes);
            }

        }
        else{
            String str = String.format("Unexpected command: id - %s, data - %s", cmdID, msg.getCommandData());
            log.error(str);
            Message mes = Message.builder()
                    .command(CommandIDs.RESPONCE_UNEXPECTEDCOMMAND)
                    .commandData(str)
                    .build();
            ctx.writeAndFlush(mes);
        }

    }

}
