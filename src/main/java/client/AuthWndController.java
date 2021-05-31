package client;

import common.CommandIDs;
import common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class AuthWndController  implements Initializable {

    @FXML
    private TextField login;
    @FXML
    private TextField password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

        FileCloudClient.netWork = new Network();

        Thread.currentThread().sleep(1000);

        Message mes = new Message();
        mes.command = CommandIDs.REQUEST_AUTHDATA;
        mes.commandData = "" + login.getText() + " " + password.getText();

        FileCloudClient.netWork.sendMessage(mes);

    }
}
