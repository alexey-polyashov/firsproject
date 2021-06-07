package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

public class ProgressController  implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private Label progressText;
    @FXML
    private ProgressBar progressIndicator;

    public void setText(String text){
        progressText.setText(text);
    }

    public void setProgress(double progress){
        progressIndicator.setProgress(progress);
    }

}
