package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {
    @FXML
    public AnchorPane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onClose(ActionEvent actionEvent) {
        ((Stage)(mainPane.getScene().getWindow())).close();
    }
}
