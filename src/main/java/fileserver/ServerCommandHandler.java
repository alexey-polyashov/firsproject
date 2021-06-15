package fileserver;

import client.FileCloudClient;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class ServerCommandHandler  extends SimpleChannelInboundHandler<Message> {

    private DBService dbService;
    private AuthService authService;
    private User usr;

    CloudFileSystem fs;

    public ServerCommandHandler(DBService dbService, AuthService authService, User usr) throws IOException {
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
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    ){
                oos.writeObject(fl);
                mes.setData(bos.toByteArray());
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
        else if(cmdID == CommandIDs.REQUEST_UPTOROOT){
            fs.goToRoot();
            Message mes = Message.builder()
                    .command(CommandIDs.RESPONCE_OK)
                    .commandData(fs.getCurrentPath())
                    .build();
            ctx.writeAndFlush(mes);
        }
        else if(cmdID == CommandIDs.REQUEST_UPTODIR){
            fs.goToParentFolder();
            Message mes = Message.builder()
                    .command(CommandIDs.RESPONCE_OK)
                    .commandData(fs.getCurrentPath())
                    .build();
            ctx.writeAndFlush(mes);
        }
        else if(cmdID == CommandIDs.REQUEST_CHANGEDIR){
            fs.changeDir(msg.getCommandData());
            Message mes = Message.builder()
                    .command(CommandIDs.RESPONCE_OK)
                    .commandData(fs.getCurrentPath())
                    .build();
            ctx.writeAndFlush(mes);
        }
        else if(cmdID == CommandIDs.REQUEST_CURRENTFOLDER){
            Message mes = Message.builder()
                    .command(CommandIDs.RESPONCE_CURRENTFOLDER)
                    .commandData(fs.getCurrentPath())
                    .build();
            ctx.writeAndFlush(mes);
        }
        else if(cmdID == CommandIDs.REQUEST_RECEIVEFILE){
            Path fName = fs.getAbsolutePathToFile(msg.getCommandData());
            log.debug("Try to send file {}", fName);
            try {
                Message mes = fs.getFilePart(fName);
                ctx.writeAndFlush(mes);
            }catch(Exception e){
                log.error("Read file error: " + e.toString());
                fs.resetChannel();
                Message mes = Message.builder()
                        .command(CommandIDs.RESPONCE_SERVERERROR)
                        .commandData(e.toString())
                        .build();
                ctx.writeAndFlush(mes);
            }
        }
        else if(cmdID == CommandIDs.REQUEST_CLIENTERROR){
            log.debug("Client error {}", msg.getCommandData());
            fs.resetChannel();
        }
        else if(cmdID == CommandIDs.REQUEST_MOVE){
            Message mes = Message.builder()
                    .command(CommandIDs.RESPONCE_OK)
                    .build();
            try {
                log.debug("Move file: {} , to {}", msg.getCommandData2(), msg.getCommandData());
                fs.move(msg.getCommandData2(), msg.getCommandData2());
            }
            catch (FileAlreadyExistsException e){
                log.error("File already exists exception: {}", msg.getCommandData());
                mes.setCommand(CommandIDs.RESPONCE_FILEEXIST);
            }
            catch (Exception e){
                log.error("Server error: {}", e.toString());
                mes.setCommand(CommandIDs.RESPONCE_SERVERERROR);
                mes.setCommandData(e.toString());
            }
            ctx.writeAndFlush(mes);
        }
        else if(cmdID == CommandIDs.REQUEST_MKDIR){
            Message mes = Message.builder()
                    .command(CommandIDs.RESPONCE_OK)
                    .build();
            try {
                log.debug("Make dir: {}", msg.getCommandData());
                fs.makeDir(Paths.get(msg.getCommandData()));
            }
            catch (FileAlreadyExistsException e){
                log.error("File already exists exception: {}", msg.getCommandData());
                mes.setCommand(CommandIDs.RESPONCE_FILEEXIST);
            }
            catch (Exception e){
                log.error("Server error: {}", e.toString());
                mes.setCommand(CommandIDs.RESPONCE_SERVERERROR);
                mes.setCommandData(e.toString());
            }
            ctx.writeAndFlush(mes);
        }
        else if(cmdID == CommandIDs.REQUEST_DELETE){

            try (
                    ByteArrayInputStream bis = new ByteArrayInputStream(msg.getData());
                    ObjectInputStream ois = new ObjectInputStream(bis);
            ) {
                List<FileInfo> fl = (List<FileInfo>) ois.readObject();
                log.debug("Prepare to delete");
                fs.prepareToDelete(fl);
                Message srvMsg = fs.deleteFromFileList();
                ctx.writeAndFlush(srvMsg);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.toString());
                Message srvMsg = Message.builder()
                        .command(CommandIDs.RESPONCE_SERVERERROR)
                        .commandData(e.toString())
                        .build();
                ctx.writeAndFlush(srvMsg);
            }

        }
        else if(cmdID == CommandIDs.REQUEST_CONTINUEDELETE){
            try {
                Message srvMsg = fs.deleteFromFileList();
                ctx.writeAndFlush(srvMsg);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.toString());
                Message srvMsg = Message.builder()
                        .command(CommandIDs.RESPONCE_SERVERERROR)
                        .commandData(e.toString())
                        .build();
                ctx.writeAndFlush(srvMsg);
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
