package client;

import common.CloudFileSystem;
import common.FileInfo;
import common.FileTypes;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class MainWndController implements Initializable {

    public CloudFileSystem fs;
    public FileCloudClient mainApp;
    @FXML
    public TextField clientPath;
    @FXML
    public TextField serverPath;

    class ClientFileInfo{
        public StringProperty file_icon = new SimpleStringProperty("");
        public StringProperty file_name = new SimpleStringProperty("");
        public LongProperty file_size = new SimpleLongProperty();
        public FileTypes file_type;

        public ClientFileInfo(String file_icon, String file_name, long file_size, FileTypes file_type) {
            this.file_icon.set(file_icon);
            this.file_name.set(file_name);
            this.file_size.set(file_size);
            this.file_type = file_type;
        }

        public String getFile_icon() {
            return file_icon.get();
        }
        public StringProperty file_iconProperty() {
            return file_icon;
        }

        public String getFile_name() {
            return file_name.get();
        }
        public StringProperty file_nameProperty() {
            return file_name;
        }


        public long getFile_size() {
            return file_size.get();
        }
        public LongProperty file_sizeProperty() {
            return file_size;
        }
    }

    @FXML
    private TableView<ClientFileInfo> serverfiles;

    @FXML
    private TableView<ClientFileInfo> clientfiles;

    private ObservableList<ClientFileInfo> clientFiletems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Path p = Paths.get("c:");
        fs = new CloudFileSystem(p, p);
        readClientFiles();
    }

    public void readClientFiles(){

        List<FileInfo> fl = fs.getFullList();

        clientfiles.getItems().removeAll();
        clientFiletems = clientfiles.getItems();
        for (FileInfo fi:fl) {
            clientFiletems.add(new ClientFileInfo(
                    fi.icon, fi.name, fi.size, fi.fileType
            ));
        }
    }

    public void clientRefreshAction(ActionEvent actionEvent) {
        readClientFiles();
    }

    public void newConnect(ActionEvent actionEvent) throws IOException {

        if(FileCloudClient.socketChannel!=null && FileCloudClient.socketChannel.isActive()) {
            FileCloudClient.ShowErrorDlg("Client already connected!\nPlease disconnect");
        }else{
            Stage authWindow = new Stage();
            authWindow.setScene(new Scene(FXMLLoader.load(getClass().getResource("authform.fxml"))));
            authWindow.initModality(Modality.WINDOW_MODAL);
            authWindow.initOwner(FileCloudClient.mainStage);
            FileCloudClient.authDlg = authWindow;
            authWindow.show();
        }
    }

    public void closeConnect(ActionEvent actionEvent) {
        if (FileCloudClient.socketChannel != null && FileCloudClient.socketChannel.isActive()) {
            FileCloudClient.socketChannel.close();
            serverfiles.getItems().clear();
            serverPath.clear();
            FileCloudClient.ShowInfoDlg("Disconnected");
        }
    }


}
