package client;


import common.CommandIDs;
import common.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientMessageHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        FileCloudClient.ShowErrorDlg(cause.getMessage());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if(msg.command == CommandIDs.RESPONCE_CONNECTIONERROR ||
            msg.command == CommandIDs.RESPONCE_AUTHERROR){
            log.error("Auth error - "+msg.commandData.toString());
            Platform.runLater(()->{
                FileCloudClient.ShowErrorDlg(msg.commandData.toString());
            });
            return;
        }
        if(msg.command == CommandIDs.RESPONCE_AUTHOK){
            log.debug("Auth successfully");
            Platform.runLater(()->{
                if(FileCloudClient.authDlg.isShowing()) {
                    FileCloudClient.authDlg.close();
                    FileCloudClient.ShowInfoDlg("Success connected");
                }
            });
        }
    }
}
