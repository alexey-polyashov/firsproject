package client;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProgressController{

    @FXML
    public Label progressText;
    @FXML
    public ProgressBar progressIndicator;
    public AnchorPane mainPanel;

    Task<Void> task;

    public void startProgress(Task<Void> task)  {

        this.task = task;

        progressIndicator.setProgress(0);
        progressIndicator.progressProperty().bind(task.progressProperty());
        progressText.textProperty().bind(task.messageProperty());
        Thread thr = new Thread(task);

        thr.setDaemon(true);
        thr.start();


    }

    public void onCancel(ActionEvent actionEvent) {
        task.cancel();
    }
}
