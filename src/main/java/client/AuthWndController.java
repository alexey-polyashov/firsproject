package client;

import common.CloudFileSystem;
import common.CommandIDs;
import common.Message;
import common.UserInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;


import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

@Slf4j
public class AuthWndController  implements Initializable {

    public AnchorPane authPane;
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

    public void enterPassword(KeyEvent keyEvent) throws InterruptedException {
        if(keyEvent.getCode()== KeyCode.ENTER){
            doLogin(new ActionEvent());
        }
    }

    public void pressedLoginBtn(KeyEvent keyEvent) throws InterruptedException {
        if(keyEvent.getCode()== KeyCode.ENTER){
            doLogin(new ActionEvent());
        }
    }

    public void remindPassword(ActionEvent actionEvent) {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Remind password");
        dialog.setContentText("Enter your email");

        ButtonType okButton = new ButtonType("Send password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField dlg_email = new TextField();
        dlg_email.setPromptText("Email");

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(okButton);
        btOk.addEventFilter(ActionEvent.ACTION,
                event -> {
                    String warning  = "";
                    if(dlg_email.getText().isEmpty()){
                        warning += "\nEmail is empty";
                        dlg_email.setStyle("-fx-border-color: red");
                    }
                    if(!warning.isEmpty()){
                        FileCloudClient.ShowInfoDlg("Fix the errors:"+warning);
                        event.consume();
                    }

                });

        gridPane.add(dlg_email, 0, 1);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> dlg_email.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                dlg_email.setStyle("");
                return dlg_email.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(userEmail -> {

            Message mes = Message.builder()
                    .command(CommandIDs.REQUEST_REMINDPASSWORD)
                    .commandData(userEmail)
                    .build();
            try{
                network.sendMessage(mes, (srvMsg, ctx)->{
                    if(srvMsg.getCommand() == CommandIDs.RESPONCE_OK){
                        log.debug("Password sent to email");
                        Platform.runLater(()->{
                            FileCloudClient.ShowInfoDlg("Password sent to email");
                            ((Stage)(authPane.getScene().getWindow())).close();
                        });
                    }else if(srvMsg.getCommand() == CommandIDs.RESPONCE_EMALEMISSING){
                        log.error("Email is missing");
                        Platform.runLater(()->{
                            FileCloudClient.ShowErrorDlg("Email is missing!");
                        });
                    }else{
                        log.error("Server error: message data id - {}, data - {}", srvMsg.getCommand(), srvMsg.getCommandData());
                        Platform.runLater(()->{
                            FileCloudClient.ShowInfoDlg("Server error: " + srvMsg.getCommandData());
                        });                    }
                });
            }catch (Exception e){
                log.error("Error - {}", e.toString());
                FileCloudClient.ShowErrorDlg("Error: " + e.toString());
            }

        });

    }

    public void registerNewUser(ActionEvent actionEvent) {

        Dialog<UserInfo> dialog = new Dialog<>();
        dialog.setTitle("Register new user");

        ButtonType okButton = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
        
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField dlg_login = new TextField();
        dlg_login.setPromptText("Login");
        TextField dlg_email = new TextField();
        dlg_email.setPromptText("Email");
        TextField dlg_password = new TextField();
        dlg_password.setPromptText("Password");

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(okButton);
        btOk.addEventFilter(ActionEvent.ACTION,
                event -> {
                    String warning  = "";
                    if(dlg_login.getText().isEmpty()){
                        warning += "\nLogin is empty";
                        dlg_login.setStyle("-fx-border-color: red");
                    }
                    if(dlg_email.getText().isEmpty()){
                        warning += "\nEmail is empty";
                        dlg_email.setStyle("-fx-border-color: red");
                    }
                    if(dlg_password.getText().isEmpty()){
                        warning += "\nPassword is empty";
                        dlg_password.setStyle("-fx-border-color: red");
                    }
                    if(!warning.isEmpty()){
                        FileCloudClient.ShowInfoDlg("Fix the errors:"+warning);
                        event.consume();
                    }else{
                        try(
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                ObjectOutputStream oos = new ObjectOutputStream(bos);
                        ){
                            Semaphore waitForCheck = new Semaphore(1);
                            waitForCheck.acquire();
                            Message mes = Message.builder()
                                    .command(CommandIDs.REQUEST_CHECKNEWUSER)
                                    .build();
                            UserInfo userInfo = UserInfo.builder()
                                    .login(dlg_login.getText())
                                    .email(dlg_email.getText())
                                    .build();
                            oos.writeObject(userInfo);
                            mes.setData(bos.toByteArray());
                            network.sendMessage(mes, (srvMsg, ctx)->{
                                log.debug("User is cheked");
                                if(srvMsg.getCommand() == CommandIDs.RESPONCE_EMALEBUSY){
                                    event.consume();
                                    Platform.runLater(()->{
                                        FileCloudClient.ShowErrorDlg("Email already taken!");
                                    });
                                }else if(srvMsg.getCommand() == CommandIDs.RESPONCE_LOGINEBUSY){
                                    event.consume();
                                    Platform.runLater(()->{
                                        FileCloudClient.ShowErrorDlg("Login already taken!");
                                    });
                                }
                                waitForCheck.release();
                            });
                            waitForCheck.acquire();
                        }catch (Exception e){
                            log.error("Error - {}", e.toString());
                            FileCloudClient.ShowErrorDlg("Error: " + e.toString());
                        }

                    }


                });

        gridPane.add(dlg_login, 0, 0);
        gridPane.add(dlg_email, 0, 1);
        gridPane.add(dlg_password, 0, 2);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> dlg_login.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                dlg_login.setStyle("");
                dlg_email.setStyle("");
                dlg_password.setStyle("");
                return UserInfo.builder()
                        .login(dlg_login.getText())
                        .email(dlg_email.getText())
                        .password(dlg_password.getText())
                        .build();
            }
            return null;
        });

        Optional<UserInfo> result = dialog.showAndWait();

        result.ifPresent(userInfo -> {

            Message mes = Message.builder()
                    .command(CommandIDs.REQUEST_NEWUSER)
                    .build();
            try(
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
            ){
                oos.writeObject(userInfo);
                mes.setData(bos.toByteArray());
                network.sendMessage(mes, (srvMsg, ctx)->{
                    log.debug("User is register");
                    if(srvMsg.getCommand() == CommandIDs.RESPONCE_OK){
                        Platform.runLater(()->{
                            FileCloudClient.ShowInfoDlg("The user is registered!");
                            ((Stage)(authPane.getScene().getWindow())).close();
                        });
                    }else if(srvMsg.getCommand() == CommandIDs.RESPONCE_EMALEBUSY){
                        Platform.runLater(()->{
                            FileCloudClient.ShowErrorDlg("Email already taken!");
                        });
                    }else if(srvMsg.getCommand() == CommandIDs.RESPONCE_LOGINEBUSY){
                        Platform.runLater(()->{
                            FileCloudClient.ShowErrorDlg("Login already taken!");
                        });
                    }else{
                        log.error("Server error: message data id - {}, data - {}", srvMsg.getCommand(), srvMsg.getCommandData());
                        Platform.runLater(()->{
                            FileCloudClient.ShowInfoDlg("Server error: " + srvMsg.getCommandData());
                        });
                    }
                });
            }catch (Exception e){
                log.error("Error - {}", e.toString());
                FileCloudClient.ShowErrorDlg("Error: " + e.toString());
            }

        });

    }
}
