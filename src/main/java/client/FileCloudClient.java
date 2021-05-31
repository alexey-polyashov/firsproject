package client;

import common.CloudFileSystem;
import io.netty.channel.ChannelId;
import io.netty.channel.socket.SocketChannel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class FileCloudClient extends Application {

    private MainWndController mainWndController;
    static Stage mainStage;
    static SocketChannel socketChannel;
    static Network netWork;
    static Stage authDlg;

    static void ShowErrorDlg(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR!");
        alert.setHeaderText(text);
        alert.showAndWait();
    }
    static void ShowInfoDlg(String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info!");
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainwnd.fxml"));
        Parent root = loader.load();

        mainStage = primaryStage;

        primaryStage.setTitle("File cloud client");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        mainWndController = loader.getController();

    }

}
