package client;

import common.CommandIDs;
import common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

@Slf4j
public class AuthWndController  implements Initializable {

    @FXML
    private TextField login;
    @FXML
    private TextField password;

    Network network;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();
    }

    public void doLogin(ActionEvent actionEvent) throws InterruptedException {
        if (login.getText().isEmpty()) {
            FileCloudClient.ShowErrorDlg("Login is empty!");
            return;
        }
        if (password.getText().isEmpty()) {
            FileCloudClient.ShowErrorDlg("Password is empty!");
            return;
        }

        Message mes = Message.builder()
                .command(CommandIDs.REQUEST_AUTHDATA)
                .commandData(login.getText() + " " + password.getText())
                .build();

        network.sendMessage(mes, (srvMsg, ctx)->{
            CommandIDs cmdID = srvMsg.getCommand();
            if(cmdID == CommandIDs.RESPONCE_CONNECTIONERROR ||
                    cmdID == CommandIDs.RESPONCE_AUTHERROR){
                log.error("Auth error - {}",srvMsg.getCommandData());
                Platform.runLater(()->{
                    FileCloudClient.ShowErrorDlg(srvMsg.getCommandData());
                });
                return;
            }
            if(cmdID == CommandIDs.RESPONCE_AUTHOK){
                FileCloudClient.setAuth(true);
                log.debug("Auth successfully");
                Platform.runLater(()->{
                    if(FileCloudClient.authDlg.isShowing()) {
                        FileCloudClient.authDlg.close();
                        FileCloudClient.ShowInfoDlg("Success connected");
                        FileCloudClient.mainWndController.getServerFiles();
                    }
                });
            }
            else{
                String s = String.format("Unexpected answer from server: commandID - %, ", (CommandIDs)cmdID);
                log.error(s);
                FileCloudClient.ShowErrorDlg(s);
            }
        });

    }
}
