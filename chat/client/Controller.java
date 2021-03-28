package chat.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;


public class Controller {

    @FXML
    public Button btnLogOut;
    @FXML
    public Button btnChangeNick;
    @FXML
    public Button btnLogIn;
    @FXML
    public VBox mainBox;
    @FXML
    public VBox loginDialog;
    @FXML
    public TextField loginName;
    @FXML
    public TextField loginPass;
    @FXML
    public TextField msgText;
    @FXML
    public TextArea mainTextArea;
    @FXML
    public VBox changeNickDialog;
    @FXML
    public Label changeNickResult;
    @FXML
    public TextField newNick;
    @FXML
    public Button btnCancelChangeNick;
    @FXML
    private Label authResult;

    public Main mainApp;


    public void showLoginDialog(boolean state){
        mainBox.setVisible(!state);
        loginDialog.setVisible(state);
        loginName.setText("");
        loginPass.setText("");
    }

    public void showChangeNickDialog(boolean state){
        mainBox.setVisible(!state);
        changeNickDialog.setVisible(state);
    }

    public void setProperties(){
        btnLogIn.setDisable(mainApp.isLogin());
        btnLogOut.setDisable(!mainApp.isLogin());
        btnChangeNick.setDisable(!mainApp.isLogin());
    }

    public void btnLogin(ActionEvent actionEvent){
        showLoginDialog(true);
    }

    public void btnLogOut(ActionEvent actionEvent) {
        mainApp.logOff();
    }

    public void btnSend(ActionEvent actionEvent) {
        mainApp.sendMessage(msgText.getText());
        msgText.setText("");
    }

    public void btnSendLogin(ActionEvent actionEvent) {
        mainApp.logIn(loginName.getText(), loginPass.getText() );
        authResult.setVisible(false);
        if(mainApp.isLogin()){
            showLoginDialog(false);
        } else if(mainApp.nickIsUsed()){
            authResult.setText("Логин уже используется");
            authResult.setVisible(true);
        } else if(mainApp.isWrongPass()){
            authResult.setText("Неверный логин пароль");
            authResult.setVisible(true);
        }else if(mainApp.isDisconnected()){
            mainTextArea.appendText("Время авторизации истекло.");
            showLoginDialog(false);
        }
    }

    public void btnCancelLogin(ActionEvent actionEvent) {
        showLoginDialog(false);
    }

    public void btnSendLoginTyped(KeyEvent keyEvent) {
        if(keyEvent.getCode()!=KeyCode.ENTER) {
            authResult.setVisible(false);
        }
    }

    public void btnChangeNick(ActionEvent actionEvent) {
        showChangeNickDialog(true);
    }

    public void onChangeNick(ActionEvent actionEvent) {
        changeNickResult.setVisible(false);
        mainApp.changeNick(newNick.getText());
    }

    public void btnCancelChangeNick(ActionEvent actionEvent) {
        showChangeNickDialog(false);
    }

    public void nickIsBusy(){
        changeNickResult.setVisible(true);
        changeNickResult.setText("Ник занят");
    }
    public void nickChangeError(){
        changeNickResult.setVisible(true);
        changeNickResult.setText("Произошла ошика на сервере");
    }

}
