package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileCloudClient extends Application {

    static MainWndController mainWndController;
    static Stage mainStage;
    static Stage authDlg;
    private static boolean authOk;

    public static boolean isAuth(){
        return authOk;
    }
    public static void setAuth(boolean p){
        FileCloudClient.authOk = p;
    }

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

        primaryStage.setTitle("File cloud client.client");
        primaryStage.setScene(new Scene(root));

        primaryStage.setOnCloseRequest(event -> {
            log.info("Main window closing");
            try {
                Network.getInstance().getWorker().shutdownGracefully();
            }catch (Exception e){

            }
        });

        primaryStage.show();

        mainWndController = loader.getController();

    }

}
