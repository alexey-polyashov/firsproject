package client;

import common.*;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.sleep;

@Slf4j
public class MainWndController implements Initializable {

    public CloudFileSystem fs;
    public FileCloudClient mainApp;
    public AtomicLong transferSize = new AtomicLong();
    public AtomicLong fullTransferSize = new AtomicLong();
    public AtomicInteger filesCount = new AtomicInteger();
    public AtomicInteger processedFiles = new AtomicInteger();
    public StringBuffer currentProcesseddFile = new StringBuffer("");
    public AtomicBoolean isTransferOn = new AtomicBoolean(false);

    private Network network;

    public boolean getTransferState(){
        return isTransferOn.get();
    }

    public double getProgress(){
        return (double)transferSize.get() / fullTransferSize.get();
    }

    public String getTextForProgress() {
        return currentProcesseddFile.toString();
    }

    @FXML
    public TextField clientPath;
    @FXML
    public TextField serverPath;
    @FXML
    public TableColumn<TableFileInfo, String> file_size;
    @FXML
    public TableColumn<TableFileInfo, String> file_name;
    @FXML
    public TableColumn<TableFileInfo, String> file_icon;
    @FXML
    public TableColumn<TableFileInfo, String> srvfile_size;
    @FXML
    public TableColumn<TableFileInfo, String> srvfile_name;
    @FXML
    public TableColumn<TableFileInfo, String> srvfile_icon;
    @FXML
    private TableView<TableFileInfo> serverFilesTab;
    @FXML
    private TableView<TableFileInfo> clientFilesTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Path p = Paths.get(System.getProperty("user.dir"));
        fs = new CloudFileSystem(p, null, false);
        network = Network.getInstance();
        readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());

        file_name.setOnEditCommit( cellData->{

            String newName = cellData.getNewValue();
            String oldName = cellData.getOldValue();

            Path newPath = Paths.get(fs.getCurrentFolder().toString(),newName);

            if(!fs.pathExists(newPath)){
                TableFileInfo rowInfo = cellData.getRowValue();
                try {
                    fs.move(oldName, newName);
                } catch (IOException e) {
                    FileCloudClient.ShowErrorDlg("Error:" + e.toString());
                }
            }else{
                FileCloudClient.ShowErrorDlg("File name already exists, rename cancel");
            }

            readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());

        });


    }

    public void receiveFromServerAction(ActionEvent actionEvent) throws InterruptedException, IOException {

        fullTransferSize.set(0);
        filesCount.set(0);
        processedFiles.set(0);
        currentProcesseddFile = new StringBuffer();

        serverFilesTab.getSelectionModel().getSelectedIndices();
        ObservableList<TableFileInfo> selFiles = serverFilesTab.getSelectionModel().getSelectedItems();

        for (TableFileInfo ti: selFiles) {
            fullTransferSize.getAndAdd(ti.getRealfile_size());
            filesCount.getAndIncrement();
        }

        log.debug("Prepare to receive {}-files, {}-bytes", filesCount.get(), fullTransferSize.get());
        isTransferOn.set(true);

//        Stage pgWindow = new Stage();
//        FXMLLoader pgLoader = new FXMLLoader(getClass().getResource("progresswnd.fxml"));
//        Parent pgParent = pgLoader.load();
//        ProgressController pgController = pgLoader.getController();
//        pgWindow.setScene(new Scene(pgParent));
//        //pgWindow.initModality(Modality.WINDOW_MODAL);
//        //pgWindow.initOwner(FileCloudClient.mainStage);
//        pgWindow.show();
//        pgController.setMainCtrl(this, pgWindow);
//        //pgController.start();

        for (TableFileInfo ti: selFiles) {

            if(ti.file_type != FileTypes.FILE){
                continue;
            }

            String fName = ti.file_name.getValue();
            currentProcesseddFile = new StringBuffer(fName);

            log.debug("Start receive file: {}", ti.getFile_name().getValue());

            AtomicInteger chunkNum = new AtomicInteger();

            Message msg = Message.builder()
                    .command(CommandIDs.REQUEST_RECEIVEFILE)
                    .commandData(fName)
                    .build();

            Semaphore waitFile = new Semaphore(1);

            waitFile.acquire();

            network.sendMessage(msg, (srvMsg, ctx) -> {
                log.debug("Receive command - {}, command data - {}", srvMsg.getCommand(), srvMsg.getCommandData());
                try {
//                    try {
//                        sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    if (srvMsg.getCommand() != CommandIDs.RESPONCE_SENDFILE) {
                        fs.resetChannel();
                        final String errorText = "Server error: " + srvMsg.getCommandData();
                        Platform.runLater(() -> {
                            FileCloudClient.ShowErrorDlg(errorText);
                        });
                        waitFile.release();
                    } else {
                        log.debug("Try to write in file {}", fName);
                        try {
                            Path fPath = fs.getAbsolutePathToFile(fName);
                            Message cliMes = fs.putFilePart(fPath, srvMsg);
                            transferSize.addAndGet(srvMsg.getLength());
                            if (!fs.isChannelReady()) {
                                chunkNum.set(0);
                                waitFile.release();
                            } else {
                                chunkNum.getAndIncrement();
                                ctx.writeAndFlush(cliMes);
                            }
                        } catch (Exception e) {
                            log.error("Save file error: " + e.toString());
                            fs.resetChannel();
                            Message cliMes = Message.builder()
                                    .command(CommandIDs.REQUEST_CLIENTERROR)
                                    .commandData(e.toString())
                                    .build();
                            ctx.writeAndFlush(cliMes);
                            chunkNum.set(0);
                            waitFile.release();
                            Platform.runLater(()->{
                                FileCloudClient.ShowErrorDlg("Error: " + e.toString());
                            });
                        }
                    }
                }catch (Exception e){
                    log.error(e.toString());
                    waitFile.release();
                    Platform.runLater(()->{
                        FileCloudClient.ShowErrorDlg("Error: " + e.toString());
                    });
                }
            });

            waitFile.acquire();
            processedFiles.getAndIncrement();

        }

        isTransferOn.set(false);
        //pgWindow.close();
        readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());

    }

    public void sendToServerAction(ActionEvent actionEvent) throws IOException {

        fullTransferSize.set(0);
        filesCount.set(0);
        processedFiles.set(0);
        currentProcesseddFile = new StringBuffer();

        clientFilesTab.getSelectionModel().getSelectedIndices();
        ObservableList<TableFileInfo> selFiles = clientFilesTab.getSelectionModel().getSelectedItems();

        for (TableFileInfo ti: selFiles) {
            fullTransferSize.getAndAdd(ti.getRealfile_size());
            filesCount.getAndIncrement();
        }

        isTransferOn.set(true);

//        Stage pgWindow = new Stage();
//        FXMLLoader pgLoader = new FXMLLoader(getClass().getResource("progresswnd.fxml"));
//        Parent pgParent = pgLoader.load();
//        ProgressController pgController = pgLoader.getController();
//        pgController.setMainCtrl(this, pgWindow);
//        pgWindow.setScene(new Scene(pgParent));
//        //pgWindow.initModality(Modality.WINDOW_MODAL);
//        //pgWindow.initOwner(FileCloudClient.mainStage);
//        pgWindow.show();
//        pgController.start();

        for (TableFileInfo ti: selFiles) {

            if(ti.file_type != FileTypes.FILE){
                continue;
            }

            String fName = ti.file_name.getValue();
            Path absPath = fs.getAbsolutePathToFile(fName);
            currentProcesseddFile = new StringBuffer(fName);

            log.debug("Start transfer file: {}", ti.getFile_name().getValue());

            int chunkNum =0;

            while (true) {

                log.debug("Transfer filepart: {}", chunkNum);

                try {
                    Message msg = fs.getFilePart(absPath);
//                    try {
//                        sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    network.sendMessage(msg, (srvMsg, ctx) -> {

                        log.debug("Receive command - {}, command data - {}", srvMsg.getCommand(), srvMsg.getCommandData());
                                if (srvMsg.getCommand() != CommandIDs.RESPONCE_FILERECIEVED) {
                                    fs.resetChannel();
                                    final String errorText = "Server error: " + srvMsg.getCommandData();
                                    Platform.runLater(() -> {
                                        FileCloudClient.ShowErrorDlg(errorText);
                                    });
                                }
                            }
                    );

                    transferSize.addAndGet(msg.getLength());
                    chunkNum++;

                }catch(Exception e){
                    log.error(e.toString());
                    fs.resetChannel();
                    break;
                }

                if(!fs.isChannelReady()){
                    break;
                }

            }

            isTransferOn.set(false);

        }

        //pgWindow.close();

        //readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());

        getServerFiles();
    }

    public void mkDirAction(ActionEvent actionEvent) {

    }

    public void deleteAction(ActionEvent actionEvent) {
    }

    public void srv_mkDirAction(ActionEvent actionEvent) {
    }

    public void srv_delete(ActionEvent actionEvent) {
    }

    public void clientFilesOnKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.F2){
            int ind = clientFilesTab.getSelectionModel().getSelectedIndex();
            clientFilesTab.edit(ind,file_name);
        }
    }

    public void clientFilesOnClick(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            TableFileInfo fileInfo = clientFilesTab.getSelectionModel().getSelectedItem();
            if(fileInfo.getFile_type() == FileTypes.ROOT_DIRECTORY){
                fs.goToRoot();
            }
            else if(fileInfo.getFile_type() == FileTypes.PARENT_DIRECTORY){
                fs.goToParentFolder();
            }
            else if(fileInfo.getFile_type() == FileTypes.DIRECTORY){
                fs.goToSubFolder(fileInfo.getFile_name().getValue());
            }
            else{
                return;
            }
            readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());
        }
    }

    public void serverFilesOnClick(MouseEvent mouseEvent) {

        if(mouseEvent.getClickCount() == 2){

            TableFileInfo fileInfo = serverFilesTab.getSelectionModel().getSelectedItem();
            Message mes = Message.builder()
                    .command(CommandIDs.REQUEST_CHANGEDIR)
                    .commandData(fileInfo.getFile_name().getValue())
                    .build();
            if(fileInfo.getFile_type() == FileTypes.ROOT_DIRECTORY){
                mes.setCommand(CommandIDs.REQUEST_UPTOROOT);
            }
            else if(fileInfo.getFile_type() == FileTypes.PARENT_DIRECTORY){
                mes.setCommand(CommandIDs.REQUEST_UPTODIR);
            }
            else if(fileInfo.getFile_type() != FileTypes.DIRECTORY){
                return;
            }

            network.sendMessage(mes, (srvMsg, ctx)->{
                    if(srvMsg.getCommand()==CommandIDs.RESPONCE_FILENOTEXISTS){
                        Platform.runLater(()->{
                            FileCloudClient.ShowErrorDlg("File not exists: " + srvMsg.getCommandData());
                        });
                    }else{
                        Platform.runLater(()->{
                            getServerFiles();
                        });
                    }
                }
            );

        }

    }

    public void clientUpDir(ActionEvent actionEvent) {
        fs.goToParentFolder();
        String dir = fs.getCurrentPath();
        Platform.runLater(()->{
            clientPath.setText(dir);
        });
        readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());
    }

    public void servertUpDir(ActionEvent actionEvent) {
        Message msg = Message.builder()
                .command(CommandIDs.REQUEST_UPTODIR)
                .build();
        network.sendMessage(msg, (srvMsg, ctx)->{
            if(srvMsg.getCommand()==CommandIDs.RESPONSE_OK) {
                Platform.runLater(() -> {
                    clientPath.setText(srvMsg.getCommandData());
                    getServerFiles();
                });
            }
        });
    }

    @Data
    class TableFileInfo{

        private final StringProperty file_icon;
        private final StringProperty file_name;
        private final StringProperty file_size;
        private final FileTypes file_type;
        private final long realfile_size;

        public TableFileInfo(String file_icon, String file_name, long file_size, FileTypes file_type) {

            String sz;
            if(file_size<1000){
                sz = String.valueOf(file_size) + "B";
            }else if(file_size<1000000){
                sz = String.valueOf(Math.round(new Long(file_size).doubleValue() / 10)/100d) + "kB";
            }else if(file_size<1000000000){
                sz = String.valueOf(Math.round(new Long(file_size).doubleValue() / 10000)/100d) + "mB";
            }else{
                sz = String.valueOf(Math.round(new Long(file_size).doubleValue() / 10000000)/100d) + "gB";
            }

            this.file_icon = new SimpleStringProperty(file_icon);
            this.file_name = new SimpleStringProperty(file_name);
            this.file_size = new SimpleStringProperty(sz);
            this.file_type = file_type;
            this.realfile_size = file_size;
        }

        public StringProperty file_iconProperty() {
            return file_icon;
        }
        public StringProperty file_nameProperty() {
            return file_name;
        }
        public StringProperty file_sizeProperty() {
            return file_size;
        }
    }

    public void readFiles(TableView<TableFileInfo> destTable,
                                TableColumn<TableFileInfo, String> file_name,
                                TableColumn<TableFileInfo, String> file_icon,
                                TableColumn<TableFileInfo, String> file_size,
                                List<FileInfo> fl){

        file_size.setCellValueFactory(cellData->cellData.getValue().file_sizeProperty());
        file_name.setCellValueFactory(cellData->cellData.getValue().file_nameProperty());
        file_icon.setCellValueFactory(cellData->cellData.getValue().file_iconProperty());

        ObservableList<TableFileInfo> clientFiles = destTable.getItems();
        clientFiles.clear();
        for (FileInfo fi:fl) {
            clientFiles.add(new TableFileInfo(fi.icon, fi.name, fi.size, fi.fileType));
        }
        clientFiles.sort((o1, o2) -> {
            if(o1.file_type==FileTypes.ROOT_DIRECTORY){
                return -1;
            }else if(o1.file_type==FileTypes.PARENT_DIRECTORY && o2.file_type != FileTypes.DIRECTORY){
                return -1;
            }else if(o1.file_type==FileTypes.FILE && o2.file_type != FileTypes.FILE){
                return 1;
            }else if(o1.file_name.greaterThanOrEqualTo(o2.file_name).get()){
                return 1;
            }
            return 0;
        });
        getClientDir();
    }

    private void getClientDir() {
        String dir = fs.getCurrentPath();
        Platform.runLater(()->{
            clientPath.setText(dir);
        });
    }

    public void getServerFiles(){
        Message mes = Message.builder()
                .command(CommandIDs.REQUEST_FILELIST)
                .build();

        network.sendMessage(mes,
                (srvMsg, ctx)->{
                    CommandIDs cmdID = srvMsg.getCommand();
                    if(cmdID == CommandIDs.RESPONCE_FILELIST){
                        log.debug("Getting files list");
                        try(
                                ByteArrayInputStream bis = new ByteArrayInputStream(srvMsg.getData());
                                ObjectInputStream ois = new ObjectInputStream(bis);
                        ) {
                            List<FileInfo> fl = (List<FileInfo>) ois.readObject();
                            Platform.runLater(()->{
                                readFiles(serverFilesTab, srvfile_name, srvfile_icon, srvfile_size, fl);
                            });
                            log.debug("Read files from server");
                        }catch (Exception e){
                            Platform.runLater(()->{
                                log.error(e.toString());
                                FileCloudClient.ShowErrorDlg(e.getMessage());
                            });
                        }
                        getServerDir();
                    }
                    else if(cmdID == CommandIDs.RESPONCE_SERVERERROR){
                        Platform.runLater(()->{
                            String s = String.format("Server error: %s", srvMsg.getCommandData());
                            log.error(s);
                            FileCloudClient.ShowErrorDlg(s);
                        });
                    }
                    else if(cmdID == CommandIDs.RESPONCE_UNEXPECTEDCOMMAND){
                        Platform.runLater(()->{
                            String s = String.format("Unexpected command");
                            log.error(s);
                            FileCloudClient.ShowErrorDlg(s);
                        });
                    }
                    else{
                        String s = String.format("Unexpected answer from server: commandID - %, ", (CommandIDs)cmdID);
                        log.error(s);
                        FileCloudClient.ShowErrorDlg(s);
                    }
                }
        );
    }

    public void getServerDir(){
        Message mes = Message.builder()
                .command(CommandIDs.REQUEST_CURRENTFOLDER)
                .build();
        log.debug("Responce to get server dir");
        network.sendMessage(mes, (srvMsg, ctx)->{
            log.debug("Get answer from server {}", srvMsg.getCommand());
                if(srvMsg.getCommand() == CommandIDs.RESPONCE_CURRENTFOLDER){
                    Platform.runLater(()->{
                        log.debug("Get answer from server {}, current dir {}", srvMsg.getCommand(), srvMsg.getCommandData());
                        serverPath.setText(srvMsg.getCommandData());
                    });
                }
            }
        );
    }

    public void serverRefreshAction(ActionEvent actionEvent) {
        getServerFiles();
    }

    public void clientRefreshAction(ActionEvent actionEvent) {
        readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());
    }

    public void newConnect(ActionEvent actionEvent) throws IOException {

        if(FileCloudClient.isAuth()) {
            FileCloudClient.ShowErrorDlg("Client already connected!");
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
        if (network.socketChannel != null && network.socketChannel.isActive()) {
            network.socketChannel.close();
            serverFilesTab.getItems().clear();
            serverPath.clear();
            FileCloudClient.ShowInfoDlg("Disconnected");
        }
    }

}
