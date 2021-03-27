package client;

import censor.Censor;
import common.InitParameters;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;


public class Main extends Application{

    final int HISTORY_LINE = 100;
    InitParameters common;
    Controller mainController;
    private boolean isLogin = false;
    private boolean wrongPass = false;
    private boolean nickIsUsed = false;
    private boolean isDisconnected = true;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket chatSocket;
    Stage primaryStage;
    FileService fileStorage;

    private String login;
    private String nick;

    private Censor censor = new Censor("censorList.txt");

    public void setNick(String newNick){
        this.nick = newNick;
        primaryStage.setTitle("Login as " + newNick + "(" + login + ")");
    }

    public boolean isLogin() {
        return isLogin;
    }

    public boolean nickIsUsed() {
        return nickIsUsed;
    }

    public boolean isWrongPass() {
        return wrongPass;
    }

    public boolean isDisconnected() {
        return isDisconnected;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        isLogin = false;
        mainController.mainApp = this;
        primaryStage.setTitle("Chat window");
        primaryStage.setScene(new Scene(root, 500, 300));
        this.primaryStage = primaryStage;
        primaryStage.show();
        mainController.setProperties();
        censor.load();
    }

    @Override
    public void stop(){
        try {
            if(isLogin) {
                sendMessage("/end");
            }
        }catch(Exception e){

        }
    }

    private void startChat() {
        try{
            this.isDisconnected = false;
            Thread thr2 = new Thread(()->{
                try {
                    String inMessage = null;
                    while(true){
                        inMessage = dis.readUTF();
                        System.out.println("get message - " + inMessage);
                        if(inMessage.equalsIgnoreCase("/end")){
                            break;
                        }
                        else if(inMessage.startsWith("/newnick")){
                            String[] parts = inMessage.split("\\s", 2);
                            Platform.runLater(() -> {
                                setNick(parts[1]);
                                mainController.showChangeNickDialog(false);
                                mainController.mainTextArea.appendText("nick changed\n");
                            });
                        }
                        else if(inMessage.startsWith("/baseerror")){
                            Platform.runLater(() -> {
                                mainController.nickChangeError();
                            });
                        }
                        else if(inMessage.startsWith("/nickbusy")){
                            Platform.runLater(() -> {
                                mainController.nickIsBusy();
                            });
                        }
                        else {
                            String appendText = inMessage + "\n";
                            fileStorage.appendText(inMessage + "\n");
                            mainController.mainTextArea.appendText(inMessage + "\n");
                        }
                    }
                    closeConnection(dos, dis);
                    System.exit(0);
                }catch (EOFException e) {
                    isDisconnected = false;
                    isLogin = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thr2.setDaemon(true);
            thr2.start();

        }catch(Exception e){
            System.out.println("exceptinon ConnectException");
            isDisconnected = true;
            isLogin = false;
            closeConnection(dos, dis);
        }

    }

    private void closeConnection(DataOutputStream dos, DataInputStream dis) {
        isDisconnected = true;
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            chatSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void logIn(String name, String pass){

        try {

            chatSocket = new Socket(common.SERVER_NAME, common.PORT);
            this.isDisconnected = false;
            dis = new DataInputStream(chatSocket.getInputStream());
            dos = new DataOutputStream(chatSocket.getOutputStream());

            isLogin = false;
            wrongPass = false;
            nickIsUsed = false;
            isDisconnected = false;

            dos.writeUTF("/auth " + name + " " + pass);
            String inMessage = dis.readUTF();
            System.out.println("get answer from server - " + inMessage);

            if(inMessage.startsWith("/authok")){

                String[] parts = inMessage.split("\\s",2);
                this.login = name;
                setNick(parts[1]);
                mainController.mainTextArea.clear();
                fileStorage = new FileStorage("history" + login + ".txt");
                List<String> hLines = fileStorage.readLines(HISTORY_LINE);
                for (String line:hLines) {
                    mainController.mainTextArea.appendText(line + "\n");
                }

                mainController.mainTextArea.appendText("Auth OK");
                mainController.mainTextArea.appendText("\n");
                isLogin = true;

            } else if (inMessage.startsWith("/nickisused")){

                nickIsUsed = true;
                isDisconnected = true;
                closeConnection(dos, dis);

            } else if (inMessage.startsWith("/wrongpass")){

                wrongPass = true;
                isDisconnected = true;
                closeConnection(dos, dis);

            }else{

                isLogin = false;
                isDisconnected = true;
                closeConnection(dos, dis);

            }

        } catch (IOException e) {

            System.out.println("close connection");
            closeConnection(dos, dis);
            e.printStackTrace();

        }

        mainController.setProperties();
        if(isLogin){
            startChat();
        }
    }

    public void logOff(){
        isDisconnected = true;
        isLogin = false;
        sendMessage("/end");
        mainController.setProperties();
        closeConnection(dos,dis);
    }

    public void sendMessage(String text){

        try {
            if(!text.trim().isEmpty()) {
                text = censor.checkContent(text);
                System.out.println("send message - " + text);
                dos.writeUTF(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
        }

    }

    public void changeNick(String text) {
        nickIsUsed = false;
        sendMessage("/changenick " + text);
    }
}
