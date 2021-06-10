package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

@Slf4j
public class ProgressController  implements Initializable {

    private MainWndController mainWndController;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        progressIndicator.setProgress(0);
        progressText.setText("Start transfer ...");

    }

    @FXML
    private Label progressText;
    @FXML
    private ProgressBar progressIndicator;

    public void setMainCtrl(MainWndController mainWndController, Stage stg){
        this.mainWndController = mainWndController;
        this.stage = stg;
    }

    public void start(){

        Thread tr = new Thread(()->{
            while(true){
                Platform.runLater(()->{
                    progressIndicator.setProgress(mainWndController.getProgress());
                    progressText.setText(mainWndController.getTextForProgress());
                });

                if(mainWndController.getTransferState()==false){
                    Platform.runLater(()-> {
                                stage.close();
                            }
                    );
                    return;
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    log.error("Progress error: {}", e.toString());
                }
            }
        });
        tr.start();

    }

}
