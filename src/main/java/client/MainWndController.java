package client;

import common.*;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import sun.nio.ch.Net;

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



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Path p = Paths.get(System.getProperty("user.dir"));
        try {
            fs = new CloudFileSystem(p, null, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        network = Network.getInstance();

        clientFilesTab.setRowFactory(
                new Callback<TableView<TableFileInfo>, TableRow<TableFileInfo>>() {
                    @Override
                    public TableRow<TableFileInfo> call(TableView<TableFileInfo> param) {
                        final TableRow<TableFileInfo> row = new TableRow<>();
                        final ContextMenu rowMenu = new ContextMenu();
                        MenuItem renameItem = new MenuItem("Rename");
                        renameItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                reNameAction(event);
                            }
                        });
                        MenuItem deleteItem = new MenuItem("Delete");
                        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
                            @SneakyThrows
                            @Override
                            public void handle(ActionEvent event) {
                                deleteAction(event);
                            }
                        });
                        MenuItem sendItem = new MenuItem("Send");
                        sendItem.setOnAction(new EventHandler<ActionEvent>() {
                            @SneakyThrows
                            @Override
                            public void handle(ActionEvent event) {
                                sendToServerAction(event);
                            }
                        });
                        MenuItem makeDir = new MenuItem("Make dir");
                        makeDir.setOnAction(new EventHandler<ActionEvent>() {
                            @SneakyThrows
                            @Override
                            public void handle(ActionEvent event) {
                                mkDirAction(event);
                            }
                        });
                        rowMenu.getItems().addAll( renameItem, deleteItem, makeDir, sendItem);
                        row.setContextMenu(rowMenu);
                        return row;
                    }
                }
        );

        serverFilesTab.setRowFactory(
                new Callback<TableView<TableFileInfo>, TableRow<TableFileInfo>>() {
                    @Override
                    public TableRow<TableFileInfo> call(TableView<TableFileInfo> param) {
                        final TableRow<TableFileInfo> row = new TableRow<>();
                        final ContextMenu rowMenu = new ContextMenu();
                        MenuItem renameItem = new MenuItem("Rename");
                        renameItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                srv_reNameAction(event);
                            }
                        });
                        MenuItem deleteItem = new MenuItem("Delete");
                        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
                            @SneakyThrows
                            @Override
                            public void handle(ActionEvent event) {
                                srv_delete(event);
                            }
                        });
                        MenuItem sendItem = new MenuItem("Receive");
                        sendItem.setOnAction(new EventHandler<ActionEvent>() {
                            @SneakyThrows
                            @Override
                            public void handle(ActionEvent event) {
                                receiveFromServerAction(event);
                            }
                        });
                        MenuItem makeDir = new MenuItem("Make dir");
                        makeDir.setOnAction(new EventHandler<ActionEvent>() {
                            @SneakyThrows
                            @Override
                            public void handle(ActionEvent event) {
                                srv_mkDirAction(event);
                            }
                        });
                        rowMenu.getItems().addAll( renameItem, deleteItem, makeDir, sendItem);
                        row.setContextMenu(rowMenu);
                        return row;
                    }
                }
        );

        readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());

    }

    public void receiveFromServerAction(ActionEvent actionEvent) throws InterruptedException, IOException {

        fullTransferSize.set(0);
        filesCount.set(0);
        processedFiles.set(0);
        currentProcesseddFile = new StringBuffer();
        transferSize.set(0);

        serverFilesTab.getSelectionModel().getSelectedIndices();
        ObservableList<TableFileInfo> selFiles = serverFilesTab.getSelectionModel().getSelectedItems();

        List<FileInfo> fileList = new ArrayList<>();

        for (TableFileInfo ti: selFiles) {
            if(ti.getFile_type() == FileTypes.DIRECTORY){
                List<FileInfo> buf = getSubFoldersInServerDir(Paths.get(ti.getFile_name().getValue()));
                fileList.addAll(buf);
                buf.clear();
                buf = getFilesInServerDir(Paths.get(ti.getFile_name().getValue()));
                fileList.addAll(buf);
                buf.clear();
            }else{
                fileList.add(new FileInfo(FileTypes.FILE, ti.getFile_name().getValue(), ti.getRealfile_size()));
            }
        }

        for (FileInfo ti : fileList) {
            fullTransferSize.getAndAdd(ti.getSize());
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

                for (FileInfo fi : fileList) {

                    if(fi.getFileType() == FileTypes.DIRECTORY){
                        log.debug("Create directory {}", fi.getName());
                        fs.makeDir(Paths.get(fi.getName()));
                        continue;
                    }

                    String fName = fi.getName();
                    currentProcesseddFile = new StringBuffer(fName);
                    updateMessage(currentProcesseddFile.toString());

                    log.debug("Start receive file: {}", fi.getName());

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

    private void setInList(List<FileInfo> dest, List<FileInfo> source){
        dest.addAll(source);
    }

    private List<FileInfo> getFilesInServerDir(Path path) throws InterruptedException {

        List<FileInfo> fileList = new ArrayList<FileInfo>();

        Message mes = Message.builder()
                .command(CommandIDs.REQUEST_RECEIVEFILESINDIR)
                .commandData(path.toString())
                .build();

        Semaphore waitForList = new Semaphore(1);
        waitForList.acquire();
        network.sendMessage(mes, (srvMes, ctx)->{
            if(srvMes.getCommand()==CommandIDs.RESPONCE_OK){
                try (
                        ByteArrayInputStream bis = new ByteArrayInputStream(srvMes.getData());
                        ObjectInputStream ois = new ObjectInputStream(bis);
                ) {
                    setInList(fileList, (List<FileInfo>) ois.readObject());
                }catch (Exception e){
                    log.error(e.toString());
                    Platform.runLater(()->{
                        FileCloudClient.ShowErrorDlg("Error: " + e.toString());
                    });
                }finally {
                    waitForList.release();
                }
            }else if (srvMes.getCommand()==CommandIDs.RESPONCE_SERVERERROR){
                log.error("Server error: {}", srvMes.getCommandData());
                waitForList.release();
                Platform.runLater(()->{
                    FileCloudClient.ShowErrorDlg("Server error: " + srvMes.getCommandData());
                });
            }else{
                log.error("Unexpected answer: id - {}, data - {}", srvMes.getCommandData());
                waitForList.release();
                Platform.runLater(()->{
                    FileCloudClient.ShowErrorDlg("Unexpected answer from server");
                });
            }
        });
        waitForList.acquire();
        return fileList;


    }

    private List<FileInfo> getSubFoldersInServerDir(Path path) throws InterruptedException {

        List<FileInfo> fileList = new ArrayList<FileInfo>();

        Message mes = Message.builder()
                .command(CommandIDs.REQUEST_RECEIVEFOLDERSINDIR)
                .commandData(path.toString())
                .build();

        Semaphore waitForList = new Semaphore(1);
        waitForList.acquire();
        network.sendMessage(mes, (srvMes, ctx)->{
            if(srvMes.getCommand()==CommandIDs.RESPONCE_OK){
                try (
                        ByteArrayInputStream bis = new ByteArrayInputStream(srvMes.getData());
                        ObjectInputStream ois = new ObjectInputStream(bis);
                ) {
                    setInList(fileList, (List<FileInfo>) ois.readObject());
                }catch (Exception e){
                    log.error(e.toString());
                    Platform.runLater(()->{
                        FileCloudClient.ShowErrorDlg("Error: " + e.toString());
                    });
                }finally {
                    waitForList.release();
                }
            }else if (srvMes.getCommand()==CommandIDs.RESPONCE_SERVERERROR){
                log.error("Server error: {}", srvMes.getCommandData());
                waitForList.release();
                Platform.runLater(()->{
                    FileCloudClient.ShowErrorDlg("Server error: " + srvMes.getCommandData());
                });
            }else{
                log.error("Unexpected answer: id - {}, data - {}", srvMes.getCommandData());
                waitForList.release();
                Platform.runLater(()->{
                    FileCloudClient.ShowErrorDlg("Unexpected answer from server");
                });
            }
        });
        waitForList.acquire();
        return fileList;

    }

    public void sendToServerAction(ActionEvent actionEvent) throws IOException {

        fullTransferSize.set(0);
        filesCount.set(0);
        processedFiles.set(0);
        currentProcesseddFile = new StringBuffer();
        transferSize.set(0);

        clientFilesTab.getSelectionModel().getSelectedIndices();
        ObservableList<TableFileInfo> selFiles = clientFilesTab.getSelectionModel().getSelectedItems();

        List<FileInfo> fileList = new ArrayList<>();

        for (TableFileInfo ti: selFiles) {
            if(ti.getFile_type() == FileTypes.DIRECTORY){
                List<FileInfo> buf = fs.getSubFoldersInDir(Paths.get(ti.getFile_name().getValue()));
                fileList.addAll(buf);
                buf.clear();
                buf = fs.getFilesInDir(Paths.get(ti.getFile_name().getValue()));
                fileList.addAll(buf);
                buf.clear();
            }else{
                fileList.add(new FileInfo(FileTypes.FILE, ti.getFile_name().getValue(), ti.getRealfile_size()));
            }
        }

        for (FileInfo fi : fileList) {
            fullTransferSize.getAndAdd(fi.getSize());
            filesCount.getAndIncrement();
        }

        log.debug("Total transfer size {}", fullTransferSize.get());
        log.debug("Total transfer files {}", filesCount.get());

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
            protected void succeeded() {
                super.succeeded();
                log.debug("Task for transfer succeeded");
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                isTransferOn.set(false);
            }

            @Override
            protected Void call() throws Exception {

                for (FileInfo fi : fileList) {

                    if(isCancelled()){
                        log.debug("Task for transfer file is canceled");
                        break;
                    }

                    if (fi.getFileType() == FileTypes.DIRECTORY) {

                        Semaphore waitForMKDir = new Semaphore(1);

                        Path absPath = fs.getAbsolutePathToFile(fi.getName());
                        String dirName = fs.getCurrentFolder().relativize(absPath).toString();

                        Message msg = Message.builder()
                                .command(CommandIDs.REQUEST_MKDIR)
                                .commandData(dirName)
                                .build();
                        log.debug("Try to make dir {}", dirName);

                        waitForMKDir.acquire();

                        network.sendMessage(msg, (srvMsg, ctx) -> {
                            try {
                                log.debug("Receive command - {}, command data - {}", srvMsg.getCommand(), srvMsg.getCommandData());
                                if (srvMsg.getCommand() == CommandIDs.RESPONCE_FILEEXIST) {
                                    cancel();
                                    Platform.runLater(() -> {
                                        FileCloudClient.ShowErrorDlg("Directory name already exists, directory don't create!");
                                    });
                                } else if (srvMsg.getCommand() != CommandIDs.RESPONCE_OK){
                                    cancel();
                                    Platform.runLater(() -> {
                                        FileCloudClient.ShowErrorDlg("Server error: " + srvMsg.getCommandData());
                                    });
                                }
                            } finally {
                                waitForMKDir.release();
                            }

                        });

                        waitForMKDir.acquire();
                        log.debug("Make dir is passed");
                        continue;

                    }

                    String fName = fi.getName();
                    Path absPath = fs.getAbsolutePathToFile(fName);
                    currentProcesseddFile = new StringBuffer(fName);
                    updateMessage(currentProcesseddFile.toString());

                    log.debug("Start transfer file: {}", fName);

                    int chunkNum = 0;

                    while (true) {

                        if(isCancelled()){
                            log.debug("Task for transfer file is canceled");
                            fs.resetChannel();
                            break;
                        }

                        try {
                            Message msg = fs.getFilePart(absPath);
                            Semaphore waitForChunk = new Semaphore(1);
                            waitForChunk.acquire();
                            log.debug("Transfer file part: {}", chunkNum);
                            network.sendMessage(msg, (srvMsg, ctx) -> {
                                try {
                                    log.debug("Receive command - {}, command data - {}", srvMsg.getCommand(), srvMsg.getCommandData());
                                    if (isTransferOn.get() == false) {
                                        fs.resetChannel();
                                    }
                                    if (srvMsg.getCommand() != CommandIDs.RESPONCE_FILERECIEVED) {
                                        log.error("Server error: {}", srvMsg.getCommandData());
                                        fs.resetChannel();
                                        final String errorText = "Server error: " + srvMsg.getCommandData();
                                        Platform.runLater(() -> {
                                            FileCloudClient.ShowErrorDlg(errorText);
                                        });
                                    }
                                }finally {
                                    waitForChunk.release();
                                }
                            });
                            waitForChunk.acquire();
                            log.debug("Transfer file part complete: {}", chunkNum);
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
                log.debug("Close progress");
                Platform.runLater(()->{
                    pgWindow.close();
                });

                getServerFiles();
                return null;
            }
        });

    }

    public void mkDirAction(ActionEvent actionEvent) {

        ObservableList<TableFileInfo> its = clientFilesTab.getItems();
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Make directory");
        dialog.setHeaderText("Enter new directory name");
        dialog.setContentText("Name");
        Optional<String> result = dialog.showAndWait();

        final String[] dirName = {""};
        result.ifPresent(name->{
            dirName[0] = name;
        });

        Path newPath = Paths.get(fs.getCurrentFolder().toString(), dirName[0]);
        if(!dirName[0].isEmpty()){
            if (!fs.pathExists(newPath)) {
                try {
                    fs.makeDir(newPath);
                    readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());
                } catch (IOException e) {
                    FileCloudClient.ShowErrorDlg("Error:" + e.toString());
                }
            }
            else {
                FileCloudClient.ShowErrorDlg("Directory name already exists, directory don't create!");
            }
        }

    }

    public void reNameAction(ActionEvent actionEvent) {

        ObservableList<TableFileInfo> its = clientFilesTab.getItems();

        int ind = clientFilesTab.getSelectionModel().getSelectedIndex();
        TableFileInfo currentRow = its.get(ind);
        String oldName = currentRow.getFile_name().getValue();

        TextInputDialog dialog = new TextInputDialog(currentRow.getFile_name().getValue());
        dialog.setTitle("Rename file/directory");
        dialog.setHeaderText("Enter new file/directory name");
        dialog.setContentText("Name");
        Optional<String> result = dialog.showAndWait();

        final String[] dirName = {""};
        result.ifPresent(name->{
            dirName[0] = name;
        });

        String newName = dirName[0];

        if(!dirName[0].isEmpty()){
            if(newName.equals(oldName)){
                return;
            }
            if (!fs.pathExists(Paths.get(newName))) {
                try {
                    fs.move(oldName, newName);
                    readFiles(clientFilesTab, file_name, file_icon, file_size, fs.getFullList());
                } catch (IOException e) {
                    FileCloudClient.ShowErrorDlg("Error:" + e.toString());
                }
            }
            else {
                FileCloudClient.ShowErrorDlg("File or directory name already exists, rename canceled!");
            }
        }

    }

    public void deleteAction(ActionEvent actionEvent) throws IOException {

        int totalFiles = 0;

        ObservableList<TableFileInfo> selectedItems = clientFilesTab.getSelectionModel().getSelectedItems();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete file");
        String descDelFiles = "";
        if(selectedItems.size()<5){
            for (TableFileInfo ti: selectedItems) {
                descDelFiles += ti.getFile_name().getValue()+"\n";
            }
        }else{
            descDelFiles = selectedItems.size() + " selected items";
        }
        alert.setHeaderText("A you sure want to delete file(s)?");
        alert.setContentText(descDelFiles);
        Optional<ButtonType> option = alert.showAndWait();
        if(option.get() != ButtonType.OK){
            return;
        }

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

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Make directory");
        dialog.setHeaderText("Enter new directory name");
        dialog.setContentText("Name");
        Optional<String> result = dialog.showAndWait();

        final String[] dirName = {""};
        result.ifPresent(name->{
            dirName[0] = name;
        });

        String newName = dirName[0];

        if(!dirName[0].isEmpty()){

            Message msg = Message.builder()
                    .command(CommandIDs.REQUEST_MKDIR)
                    .commandData(newName)
                    .build();

            network.sendMessage(msg, (srvMsg, ctx) -> {
                if(srvMsg.getCommand() == CommandIDs.RESPONCE_OK){
                    Platform.runLater(() -> {
                        getServerFiles();
                    });
                }else if (srvMsg.getCommand() == CommandIDs.RESPONCE_FILEEXIST) {
                    Platform.runLater(() -> {
                        FileCloudClient.ShowErrorDlg("Directory name already exists, directory don't create!");
                    });
                }else{
                    Platform.runLater(() -> {
                        FileCloudClient.ShowErrorDlg("Server error: " + srvMsg.getCommandData());
                    });
                }
            });
        }

    }

    public void srv_reNameAction(ActionEvent actionEvent) {

        ObservableList<TableFileInfo> its = serverFilesTab.getItems();
        int ind = serverFilesTab.getSelectionModel().getSelectedIndex();
        TableFileInfo currentRow = its.get(ind);
        String oldName = currentRow.getFile_name().getValue();

        TextInputDialog dialog = new TextInputDialog(currentRow.getFile_name().getValue());
        dialog.setTitle("Rename file/directory");
        dialog.setHeaderText("Enter new file/directory name");
        dialog.setContentText("Name");
        Optional<String> result = dialog.showAndWait();

        final String[] dirName = {""};
        result.ifPresent(name->{
            dirName[0] = name;
        });

        String newName = dirName[0];

        if(!dirName[0].isEmpty()){
            if(newName.equals(oldName)){
                return;
            }

            Message msg = Message.builder()
                    .command(CommandIDs.REQUEST_MOVE)
                    .commandData(newName)
                    .commandData2(oldName)
                    .build();

            network.sendMessage(msg, (srvMsg, ctx) -> {
                if(srvMsg.getCommand() == CommandIDs.RESPONCE_OK){
                    Platform.runLater(() -> {
                        getServerFiles();
                    });
                }else if (srvMsg.getCommand() == CommandIDs.RESPONCE_FILEEXIST) {
                    Platform.runLater(() -> {
                        FileCloudClient.ShowErrorDlg("File with such name already exist, rename canceled!");
                    });
                }else{
                    Platform.runLater(() -> {
                        FileCloudClient.ShowErrorDlg("Server error: " + srvMsg.getCommandData());
                    });
                }
            });
        }

    }

    public void srv_delete(ActionEvent actionEvent) throws IOException {

        ObservableList<TableFileInfo> selectedItems = serverFilesTab.getSelectionModel().getSelectedItems();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete file");
        String descDelFiles = "";
        if(selectedItems.size()<5){
            for (TableFileInfo ti: selectedItems) {
                descDelFiles += ti.getFile_name().getValue()+"\n";
            }
        }else{
            descDelFiles = selectedItems.size() + " selected items";
        }
        alert.setHeaderText("A you sure want to delete file(s)?");
        alert.setContentText(descDelFiles);
        Optional<ButtonType> option = alert.showAndWait();
        if(option.get() != ButtonType.OK){
            return;
        }

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
                                    Platform.runLater(()->{
                                        pgWindow.close();
                                        getServerFiles();
                                    });
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

    public void serverUpDir(ActionEvent actionEvent) {
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

        log.debug("Getting files list from server");

        Message mes = Message.builder()
                .command(CommandIDs.REQUEST_FILELIST)
                .build();

        network.sendMessage(mes,
                (srvMsg, ctx) -> {
                    CommandIDs cmdID = srvMsg.getCommand();
                    if (cmdID == CommandIDs.RESPONCE_FILELIST) {
                        log.debug("Get response files list");
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
                            log.error("Error: {}", e.toString());
                            Platform.runLater(() -> {
                                FileCloudClient.ShowErrorDlg(e.getMessage());
                            });
                        }
                        getServerDir();
                    } else if (cmdID == CommandIDs.RESPONCE_SERVERERROR) {
                        log.error("Server error: {}", srvMsg.getCommandData());
                        Platform.runLater(() -> {
                            String s = String.format("Server error: %s", srvMsg.getCommandData());
                            FileCloudClient.ShowErrorDlg(s);
                        });
                    } else if (cmdID == CommandIDs.RESPONCE_UNEXPECTEDCOMMAND) {
                        log.error("Unexpected command");
                        Platform.runLater(() -> {
                            String s = String.format("Unexpected command");
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

            network = Network.getInstance();

        }
    }

    public void closeConnect(ActionEvent actionEvent) {
        if (network.getSocketChannel() != null && network.getSocketChannel().isActive()) {
            Network.shutDown();
            serverFilesTab.getItems().clear();
            serverPath.clear();
            FileCloudClient.setAuth(false);
            FileCloudClient.ShowInfoDlg("Disconnected");
        }
    }

}
