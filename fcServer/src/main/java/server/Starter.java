package server;

import client.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Starter extends Application {

//    public static void main(String[] args) {
//        new MainServer().start();
//    }

    Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
        Parent root = loader.load();
        mainStage = primaryStage;
        primaryStage.setScene(new Scene(root));

        primaryStage.show();

    }



}
