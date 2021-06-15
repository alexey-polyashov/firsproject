package client;

import common.*;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.util.Callback;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
public class MainWndController implements Initializable {

    public CloudFileSystem fs;
    public FileCloudClient mainApp;
    public AtomicLong transferSize = new AtomicLong();
    public AtomicLong fullTransferSize = new AtomicLong();
    public AtomicInteger filesCount = new AtomicInteger();
    public AtomicInteger processedFiles = new AtomicInteger();
    public StringBuffer currentProcesseddFile = new StringBuffer("");
    public volatile AtomicBoolean isTransferOn = new AtomicBoolean(false);

    private boolean notApplyFocusChange = false;
    private boolean forceEditCell = false;
    private boolean forceMakeDir = false;

    private Network network;

    public boolean getTransferState() {
        return isTransferOn.get();
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

    @Data
    class TableFileInfo {

        private final StringProperty file_icon;
        private final StringProperty file_name;
        private final StringProperty file_size;
        private final FileTypes file_type;
        private final long realfile_size;
        private boolean editIsOn = false;

        public TableFileInfo(String file_icon, String file_name, long file_size, FileTypes file_type) {

            String sz;
            if (file_size < 1000) {
                sz = String.valueOf(file_size) + "B";
            } else if (file_size < 1000000) {
                sz = String.valueOf(Math.round(new Long(file_size).doubleValue() / 10) / 100d) + "kB";
            } else if (file_size < 1000000000) {
                sz = String.valueOf(Math.round(new Long(file_size).doubleValue() / 10000) / 100d) + "mB";
            } else {
                sz = String.valueOf(Math.round(new Long(file_size).doubleValue() / 10000000) / 100d) + "gB";
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

    class EditingCell extends TableCell<TableFileInfo, String>{

        private TextField textField;


        public EditingCell() {
        }


        @Override
        public void startEdit() {
            if (forceEditCell || !isEmpty()) {
                TableView<TableFileInfo> currentTable = this.getTableView();
                TableFileInfo ti = currentTable.getItems().get(currentTable.getEditingCell().getRow());
                if(!forceEditCell && ti.isEditIsOn()!=true && ti.getFile_type()!=FileTypes.FILE){
                    cancelEdit();
                }
                else{
                    super.startEdit();
                    ti.setEditIsOn(false);
                    createTextField();
                    setText(null);
                    setGraphic(textField);
                    textField.selectAll();
                }
            }
            forceEditCell = false;
        }


        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void commitEdit(String newValue) {
            super.commitEdit(newValue);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
            textField.setOnKeyPressed(event->{
                if(event.getCode()==KeyCode.ENTER) {
                    commitEdit(textField.getText());
                }
            });
            textField.focusedProperty().addListener(new ChangeListener<Boolean>(){
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0,
                                    Boolean arg1, Boolean arg2) {
                    if (!arg2) {
                        if(notApplyFocusChange == true){
                            notApplyFocusChange = false;
                        }else {
                            commitEdit(textField.getText());
                        }
                    }
                }

            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Path p = Paths.get(System.getProperty("user.dir"));
        try {
            fs = new CloudFileSystem(p, null, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        network = Network.getInstance();

        readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());

        Callback<TableColumn<TableFileInfo, String>, TableCell<TableFileInfo, String>> cellFactory =
                new Callback<TableColumn<TableFileInfo, String>, TableCell<TableFileInfo, String>>() {
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };

        file_name.setCellFactory(cellFactory);
        file_name.setOnEditCommit(cellData -> {

            notApplyFocusChange = true;

            String newName = cellData.getNewValue();
            String oldName = cellData.getOldValue();

            Path newPath = Paths.get(fs.getCurrentFolder().toString(), newName);

            if(forceMakeDir){
                if (!fs.pathExists(newPath)) {
                    try {
                        fs.makeDir(newPath);
                    } catch (IOException e) {
                        FileCloudClient.ShowErrorDlg("Error:" + e.toString());
                    }
                }
                else {
                    clientFilesTab.getItems().get(cellData.getTableView().getEditingCell().getRow()).setEditIsOn(false);
                    FileCloudClient.ShowErrorDlg("Directory name already exists, directory don't create!");
                }
            }
            else {
                if(newName.equals(oldName)){
                    return;
                }
                if (!fs.pathExists(newPath)) {
                    try {
                        fs.move(oldName, newName);
                    } catch (IOException e) {
                        FileCloudClient.ShowErrorDlg("Error:" + e.toString());
                    }
                }
                else {
                    clientFilesTab.getItems().get(cellData.getTableView().getEditingCell().getRow()).setEditIsOn(false);
                    FileCloudClient.ShowErrorDlg("File name already exists, rename cancel");
                }
            }
            forceMakeDir = false;
            readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());

        });

        srvfile_name.setCellFactory(cellFactory);
        srvfile_name.setOnEditCommit(cellData -> {

            notApplyFocusChange = true;

            String newName = cellData.getNewValue();
            String oldName = cellData.getOldValue();

            if(forceMakeDir){

                Message msg = Message.builder()
                        .command(CommandIDs.REQUEST_MKDIR)
                        .commandData(newName)
                        .build();

                network.sendMessage(msg, (srvMsg, ctx) -> {
                    if (srvMsg.getCommand() == CommandIDs.RESPONCE_FILEEXIST) {
                        Platform.runLater(() -> {
                            FileCloudClient.ShowErrorDlg("Directory name already exists, directory don't create!");
                        });
                    } else if (srvMsg.getCommand() != CommandIDs.RESPONCE_OK) {
                        Platform.runLater(() -> {
                            FileCloudClient.ShowErrorDlg("Server error: " + srvMsg.getCommandData());
                        });
                    }
                });

            }else {

                if(newName.equals(oldName)){
                    return;
                }

                Message msg = Message.builder()
                        .command(CommandIDs.REQUEST_MOVE)
                        .commandData(newName)
                        .commandData2(oldName)
                        .build();

                network.sendMessage(msg, (srvMsg, ctx) -> {
                    if (srvMsg.getCommand() == CommandIDs.RESPONCE_FILEEXIST) {
                        Platform.runLater(() -> {
                            FileCloudClient.ShowErrorDlg("File with such name already exist, rename canceled!");
                        });
                    } else if (srvMsg.getCommand() != CommandIDs.RESPONCE_OK) {
                        Platform.runLater(() -> {
                            FileCloudClient.ShowErrorDlg("Server error: " + srvMsg.getCommandData());
                        });
                    }
                });
            }
            forceMakeDir = false;
            getServerFiles();

        });

    }

    public void receiveFromServerAction(ActionEvent actionEvent) throws InterruptedException, IOException {

        fullTransferSize.set(0);
        filesCount.set(0);
        processedFiles.set(0);
        currentProcesseddFile = new StringBuffer();
        transferSize.set(0);

        serverFilesTab.getSelectionModel().getSelectedIndices();
        ObservableList<TableFileInfo> selFiles = serverFilesTab.getSelectionModel().getSelectedItems();

        for (TableFileInfo ti : selFiles) {
            fullTransferSize.getAndAdd(ti.getRealfile_size());
            filesCount.getAndIncrement();
        }

        log.debug("Prepare to receive {}-files, {}-bytes", filesCount.get(), fullTransferSize.get());
        isTransferOn.set(true);

        Stage pgWindow = new Stage();
        FXMLLoader pgLoader = new FXMLLoader(getClass().getResource("progresswnd.fxml"));
        Parent pgParent = pgLoader.load();
        ProgressController pgController = pgLoader.getController();
        pgWindow.setScene(new Scene(pgParent));
        pgWindow.initModality(Modality.WINDOW_MODAL);
        pgWindow.initOwner(FileCloudClient.mainStage);
        pgWindow.show();


        pgController.startProgress(new Task<Void>() {
            @Override
            protected void cancelled() {
                super.cancelled();
                isTransferOn.set(false);
            }

            @Override
            protected Void call() throws Exception {

                for (TableFileInfo ti : selFiles) {

                    if (ti.file_type != FileTypes.FILE) {
                        continue;
                    }

                    String fName = ti.file_name.getValue();
                    currentProcesseddFile = new StringBuffer(fName);
                    updateMessage(currentProcesseddFile.toString());

                    log.debug("Start receive file: {}", ti.getFile_name().getValue());

                    AtomicInteger chunkNum = new AtomicInteger();

                    Message msg = Message.builder()
                            .command(CommandIDs.REQUEST_RECEIVEFILE)
                            .commandData(fName)
                            .build();

                    Semaphore waitFile = new Semaphore(1);

                    try {
                        waitFile.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    network.sendMessage(msg, (srvMsg, ctx) -> {
                        log.debug("Receive command - {}, command data - {}", srvMsg.getCommand(), srvMsg.getCommandData());
                        try {
                            if (isTransferOn.get() == false) {
                                fs.resetChannel();
                            }
                            if (srvMsg.getCommand() != CommandIDs.RESPONCE_SENDFILE) {
                                fs.resetChannel();
                                final String errorText = "Server error: " + srvMsg.getCommandData();
                                Platform.runLater(() -> {
                                    FileCloudClient.ShowErrorDlg(errorText);
                                });
                                waitFile.release();
                                cancel();
                            } else {
                                log.debug("Try to write in file {}", fName);
                                try {
                                    Path fPath = fs.getAbsolutePathToFile(fName);
                                    Message cliMes = fs.putFilePart(fPath, srvMsg);
                                    transferSize.addAndGet(srvMsg.getPartLen());
                                    updateProgress(transferSize.get(), fullTransferSize.get());
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
                                    cancel();
                                    Platform.runLater(() -> {
                                        FileCloudClient.ShowErrorDlg("Error: " + e.toString());
                                    });
                                }
                            }
                        } catch (Exception e) {
                            log.error(e.toString());
                            waitFile.release();
                            Platform.runLater(() -> {
                                FileCloudClient.ShowErrorDlg("Error: " + e.toString());
                            });
                        }

                    });

                    try {
                        waitFile.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    processedFiles.getAndIncrement();

                }
                Platform.runLater(() -> {
                    pgWindow.close();
                });
                isTransferOn.set(false);
                readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());

                return null;
            }

        });

    }

    public void sendToServerAction(ActionEvent actionEvent) throws IOException {

        fullTransferSize.set(0);
        filesCount.set(0);
        processedFiles.set(0);
        currentProcesseddFile = new StringBuffer();
        transferSize.set(0);

        clientFilesTab.getSelectionModel().getSelectedIndices();
        ObservableList<TableFileInfo> selFiles = clientFilesTab.getSelectionModel().getSelectedItems();

        for (TableFileInfo ti : selFiles) {
            fullTransferSize.getAndAdd(ti.getRealfile_size());
            filesCount.getAndIncrement();
        }

        isTransferOn.set(true);

        Stage pgWindow = new Stage();
        FXMLLoader pgLoader = new FXMLLoader(getClass().getResource("progresswnd.fxml"));
        Parent pgParent = pgLoader.load();
        ProgressController pgController = pgLoader.getController();
        pgWindow.setScene(new Scene(pgParent));
        pgWindow.initModality(Modality.WINDOW_MODAL);
        pgWindow.initOwner(FileCloudClient.mainStage);
        pgWindow.show();

        pgController.startProgress(new Task<Void>() {
            @Override
            protected void cancelled() {
                isTransferOn.set(false);
                super.cancelled();
            }

            @Override
            protected Void call() throws Exception {


                for (TableFileInfo ti : selFiles) {

                    if (ti.file_type != FileTypes.FILE) {
                        continue;
                    }

                    String fName = ti.file_name.getValue();
                    Path absPath = fs.getAbsolutePathToFile(fName);
                    currentProcesseddFile = new StringBuffer(fName);
                    updateMessage(currentProcesseddFile.toString());

                    log.debug("Start transfer file: {}", ti.getFile_name().getValue());

                    int chunkNum = 0;

                    while (true) {

                        log.debug("Transfer filepart: {}", chunkNum);

                        try {
                            Message msg = fs.getFilePart(absPath);
                            network.sendMessage(msg, (srvMsg, ctx) -> {
                                        if(isTransferOn.get()==false){
                                            fs.resetChannel();
                                        }
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

                            transferSize.addAndGet(msg.getPartLen());
                            updateProgress(transferSize.get(), fullTransferSize.get());
                            chunkNum++;

                        } catch (Exception e) {
                            log.error(e.toString());
                            fs.resetChannel();
                            break;
                        }

                        if (!fs.isChannelReady()) {
                            break;
                        }
                        if (isTransferOn.get() == false) {
                            fs.resetChannel();
                            break;
                        }

                    }

                    if (isTransferOn.get() == false) {
                        break;
                    }

                }

                isTransferOn.set(false);
                pgWindow.close();

                getServerFiles();
                return null;
            }
        });

    }

    public void mkDirAction(ActionEvent actionEvent) {
        ObservableList<TableFileInfo> its = clientFilesTab.getItems();
        int coincident = 0;
        for (TableFileInfo tInf: its) {
            if(tInf.getFile_name().getValue().startsWith("Новая папка")){
                coincident++;
            }
        }
        its.add(new TableFileInfo("D", "Новая папка" + (coincident==0 ? "": " " + coincident),0, FileTypes.DIRECTORY));
        clientFilesTab.getSelectionModel().select(its.size()-1);
        forceEditCell = true;
        forceMakeDir = true;
        Platform.runLater(()->{
            clientFilesTab.edit(its.size()-1, file_name);
        });

    }

    public void deleteAction(ActionEvent actionEvent) throws IOException {

        int totalFiles = 0;

        ObservableList<TableFileInfo> selectedItems = clientFilesTab.getSelectionModel().getSelectedItems();

        List<FileInfo> fileList = new ArrayList<>();

        for (TableFileInfo ti:selectedItems) {
            if(ti.getFile_type() == FileTypes.DIRECTORY){
                List<FileInfo> buf = fs.getFilesInDir(Paths.get(ti.getFile_name().getValue()));
                fileList.addAll(buf);
                totalFiles += buf.size();
                buf.clear();
                buf = fs.getSubFoldersInDir(Paths.get(ti.getFile_name().getValue()));
                Collections.reverse(buf);
                fileList.addAll(buf);
                totalFiles += buf.size();
                buf.clear();
            }else{
                fileList.add(new FileInfo(FileTypes.FILE, ti.getFile_name().getValue(), ti.getRealfile_size()));
                totalFiles++;
            }
        }

        Stage pgWindow = new Stage();
        FXMLLoader pgLoader = new FXMLLoader(getClass().getResource("progresswnd.fxml"));
        Parent pgParent = pgLoader.load();
        ProgressController pgController = pgLoader.getController();
        pgWindow.setScene(new Scene(pgParent));
        pgWindow.initModality(Modality.WINDOW_MODAL);
        pgWindow.initOwner(FileCloudClient.mainStage);
        pgWindow.show();

        final int finalTotalFiles = totalFiles;
        pgController.startProgress(new Task<Void>() {

            @Override
            protected void cancelled() {
                super.cancelled();

                Platform.runLater(() -> {
                    pgWindow.close();
                });

                readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());

            }

            @Override
            protected Void call() throws Exception {

                int currentProgress = 0;

                for (FileInfo fi : fileList) {
                    try {
                        fs.delete(Paths.get(fi.getName()));
                        updateProgress(++currentProgress, finalTotalFiles);
                        updateMessage(fi.getName());
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            log.error("Error deleting file: {}", fi.getName());
                            FileCloudClient.ShowErrorDlg("Error in file: " + fi.getName());
                        });
                        cancel();
                        return null;
                    }
                }

                Platform.runLater(() -> {
                    pgWindow.close();
                });

                readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());

                return null;
            }

        });

    }

    public void srv_mkDirAction(ActionEvent actionEvent) {

        ObservableList<TableFileInfo> its = serverFilesTab.getItems();
        int coincident = 0;
        for (TableFileInfo tInf: its) {
            if(tInf.getFile_name().getValue().startsWith("Новая папка")){
                coincident++;
            }
        }
        its.add(new TableFileInfo("D", "Новая папка" + (coincident==0 ? "": " " + coincident),0, FileTypes.DIRECTORY));
        serverFilesTab.getSelectionModel().select(its.size()-1);
        forceEditCell = true;
        forceMakeDir = true;
        serverFilesTab.edit(its.size()-1, srvfile_name);
    }

    public void srv_delete(ActionEvent actionEvent) throws IOException {

        ObservableList<TableFileInfo> selectedItems = serverFilesTab.getSelectionModel().getSelectedItems();

        List<FileInfo> fileList = new ArrayList<>();

        for (TableFileInfo ti : selectedItems) {
            fileList.add(new FileInfo(ti.getFile_type(), ti.getFile_name().getValue(), ti.getRealfile_size()));
        }

        Stage pgWindow = new Stage();
        FXMLLoader pgLoader = new FXMLLoader(getClass().getResource("progresswnd.fxml"));
        Parent pgParent = pgLoader.load();
        ProgressController pgController = pgLoader.getController();
        pgWindow.setScene(new Scene(pgParent));
        pgWindow.initModality(Modality.WINDOW_MODAL);
        pgWindow.initOwner(FileCloudClient.mainStage);
        pgWindow.show();

        pgController.startProgress(new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                log.debug("Start delete files");
                updateMessage("Prepare to delete ...");

                try (
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(bos);) {
                    oos.writeObject(fileList);
                    Message msg = Message.builder()
                            .command(CommandIDs.REQUEST_DELETE)
                            .data(bos.toByteArray())
                            .build();
                    network.sendMessage(msg, (srvMsg, ctx) -> {
                                if (this.isCancelled()) {
                                    log.debug("Delete canceled");
                                }
                                log.debug("Receive command - {}, command data - {}", srvMsg.getCommand(), srvMsg.getCommandData());
                                if (srvMsg.getCommand() == CommandIDs.RESPONCE_DELETEPROGRESS) {
                                    updateProgress(srvMsg.getPartNum(), srvMsg.getPartLen());
                                    updateMessage(srvMsg.getCommandData());
                                    Message cliMes = Message.builder()
                                        .command(CommandIDs.REQUEST_CONTINUEDELETE)
                                        .build();
                                    ctx.writeAndFlush(cliMes);
                                } else if (srvMsg.getCommand() == CommandIDs.RESPONCE_SERVERERROR) {
                                    final String errorText = "Server error: " + srvMsg.getCommandData();
                                    Platform.runLater(() -> {
                                        FileCloudClient.ShowErrorDlg(errorText);
                                    });
                                }else if (srvMsg.getCommand() == CommandIDs.RESPONCE_OK) {
                                    updateProgress(1, 1);
                                    updateMessage(srvMsg.getCommandData());
                                } else {
                                    final String errorText = "Enexpected answer from server: " + srvMsg.getCommandData();
                                    Platform.runLater(() -> {
                                        FileCloudClient.ShowErrorDlg(errorText);
                                    });
                                }
                            }
                    );

                } catch (Exception e) {
                    log.error(e.toString());
                    Platform.runLater(() -> {
                        FileCloudClient.ShowErrorDlg(e.toString());
                    });
                }

                pgWindow.close();

                getServerFiles();
                return null;
            }

        });

    }

    public void clientFilesOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.F2) {
            int ind = clientFilesTab.getSelectionModel().getSelectedIndex();
            clientFilesTab.getItems().get(ind).setEditIsOn(true);
            clientFilesTab.edit(ind, file_name);
        }
    }

    public void serverFilesOnKeyPressed(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.F2) {
            int ind = serverFilesTab.getSelectionModel().getSelectedIndex();
            serverFilesTab.getItems().get(ind).setEditIsOn(true);
            serverFilesTab.edit(ind, srvfile_name);
        }
    }

    public void clientFilesOnClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            TableFileInfo fileInfo = clientFilesTab.getSelectionModel().getSelectedItem();
            if (fileInfo.getFile_type() == FileTypes.ROOT_DIRECTORY) {
                fs.goToRoot();
                readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());
            } else if (fileInfo.getFile_type() == FileTypes.PARENT_DIRECTORY) {
                fs.goToParentFolder();
                readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());
            } else if (fileInfo.getFile_type() == FileTypes.DIRECTORY) {
                fs.goToSubFolder(fileInfo.getFile_name().getValue());
                readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());
            } else {
                return;
            }

        }
    }

    public void serverFilesOnClick(MouseEvent mouseEvent) {

        if (mouseEvent.getClickCount() == 2) {

            TableFileInfo fileInfo = serverFilesTab.getSelectionModel().getSelectedItem();
            Message mes = Message.builder()
                    .command(CommandIDs.REQUEST_CHANGEDIR)
                    .commandData(fileInfo.getFile_name().getValue())
                    .build();
            if (fileInfo.getFile_type() == FileTypes.ROOT_DIRECTORY) {
                mes.setCommand(CommandIDs.REQUEST_UPTOROOT);
            } else if (fileInfo.getFile_type() == FileTypes.PARENT_DIRECTORY) {
                mes.setCommand(CommandIDs.REQUEST_UPTODIR);
            } else if (fileInfo.getFile_type() != FileTypes.DIRECTORY) {
                return;
            }

            network.sendMessage(mes, (srvMsg, ctx) -> {
                        if (srvMsg.getCommand() == CommandIDs.RESPONCE_FILENOTEXISTS) {
                            Platform.runLater(() -> {
                                FileCloudClient.ShowErrorDlg("File not exists: " + srvMsg.getCommandData());
                            });
                        } else {
                            Platform.runLater(() -> {
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
        Platform.runLater(() -> {
            clientPath.setText(dir);
        });
        readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());
    }

    public void servertUpDir(ActionEvent actionEvent) {
        Message msg = Message.builder()
                .command(CommandIDs.REQUEST_UPTODIR)
                .build();
        network.sendMessage(msg, (srvMsg, ctx) -> {
            if (srvMsg.getCommand() == CommandIDs.RESPONCE_OK) {
                Platform.runLater(() -> {
                    clientPath.setText(srvMsg.getCommandData());
                    getServerFiles();
                });
            }
        });
    }


    public void readFiles(TableView<TableFileInfo> destTable,
                          TableColumn<TableFileInfo, String> file_name,
                          TableColumn<TableFileInfo, String> file_icon,
                          TableColumn<TableFileInfo, String> file_size,
                          List<FileInfo> fl) {

        file_size.setCellValueFactory(cellData -> cellData.getValue().file_sizeProperty());
        file_name.setCellValueFactory(cellData -> cellData.getValue().file_nameProperty());
        file_icon.setCellValueFactory(cellData -> cellData.getValue().file_iconProperty());

        ObservableList<TableFileInfo> clientFiles = destTable.getItems();
        clientFiles.clear();
        for (FileInfo fi : fl) {
            clientFiles.add(new TableFileInfo(fi.getIcon(), fi.getName(), fi.getSize(), fi.getFileType()));
        }
        clientFiles.sort((o1, o2) -> {
            if (o1.file_type == FileTypes.ROOT_DIRECTORY) {
                return -1;
            } else if (o1.file_type == FileTypes.PARENT_DIRECTORY && o2.file_type != FileTypes.DIRECTORY) {
                return -1;
            } else if (o1.file_type == FileTypes.FILE && o2.file_type != FileTypes.FILE) {
                return 1;
            } else if (o1.file_name.greaterThanOrEqualTo(o2.file_name).get()) {
                return 1;
            }
            return 0;
        });
        getClientDir();
    }

    private void getClientDir() {
        String dir = fs.getCurrentPath();
        Platform.runLater(() -> {
            clientPath.setText(dir);
        });
    }

    public void getServerFiles() {
        Message mes = Message.builder()
                .command(CommandIDs.REQUEST_FILELIST)
                .build();

        network.sendMessage(mes,
                (srvMsg, ctx) -> {
                    CommandIDs cmdID = srvMsg.getCommand();
                    if (cmdID == CommandIDs.RESPONCE_FILELIST) {
                        log.debug("Getting files list");
                        try (
                                ByteArrayInputStream bis = new ByteArrayInputStream(srvMsg.getData());
                                ObjectInputStream ois = new ObjectInputStream(bis);
                        ) {
                            List<FileInfo> fl = (List<FileInfo>) ois.readObject();
                            Platform.runLater(() -> {
                                readFiles(serverFilesTab, srvfile_name, srvfile_icon, srvfile_size, fl);
                            });
                            log.debug("Read files from server");
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                log.error(e.toString());
                                FileCloudClient.ShowErrorDlg(e.getMessage());
                            });
                        }
                        getServerDir();
                    } else if (cmdID == CommandIDs.RESPONCE_SERVERERROR) {
                        Platform.runLater(() -> {
                            String s = String.format("Server error: %s", srvMsg.getCommandData());
                            log.error(s);
                            FileCloudClient.ShowErrorDlg(s);
                        });
                    } else if (cmdID == CommandIDs.RESPONCE_UNEXPECTEDCOMMAND) {
                        Platform.runLater(() -> {
                            String s = String.format("Unexpected command");
                            log.error(s);
                            FileCloudClient.ShowErrorDlg(s);
                        });
                    } else {
                        String s = String.format("Unexpected answer from server: commandID - %, ", (CommandIDs) cmdID);
                        log.error(s);
                        FileCloudClient.ShowErrorDlg(s);
                    }
                }
        );
    }

    public void getServerDir() {
        Message mes = Message.builder()
                .command(CommandIDs.REQUEST_CURRENTFOLDER)
                .build();
        log.debug("Responce to get server dir");
        network.sendMessage(mes, (srvMsg, ctx) -> {
                    log.debug("Get answer from server {}", srvMsg.getCommand());
                    if (srvMsg.getCommand() == CommandIDs.RESPONCE_CURRENTFOLDER) {
                        Platform.runLater(() -> {
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

        if (FileCloudClient.isAuth()) {
            FileCloudClient.ShowErrorDlg("Client already connected!");
        } else {
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
