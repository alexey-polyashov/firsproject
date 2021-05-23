package io;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatController implements Initializable {

    public TextField input;
    public ListView<String> listView;

    private DataInputStream is;
    private DataOutputStream os;

    ObjectOutputStream oos;

    private String filename;
    private FilePart filePart;
    private TransferState state = TransferState.WAIT_TRANSFER;


    private FileInputStream fis;
    private int bufferSize = 8192;
    private int partNum =0;

    public void send(ActionEvent actionEvent) throws IOException {

        filename = input.getText();
        state = TransferState.START_TRANSFER;

        os.writeUTF("StartFileTransfer");

        os.flush();
        input.clear();
    }

    private FilePart readFilePart() throws IOException {

        log.debug("Read part: " + partNum);
        byte[] data = new byte[bufferSize];
        int len =0;
        if((len = fis.read(data)) != -1) {
            filePart = new FilePart(filename, len, data, partNum++, len<bufferSize);
            log.debug("File part is read");
            return filePart;
        }
        else{
            log.debug("File not read");
            return null;
        }

    }

    private void transferPart(FilePart fp, DataOutputStream os) throws IOException {
        oos.writeObject(fp);
        oos.flush();
        log.debug("Transfered");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            oos = new ObjectOutputStream(os);
            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        String msg = is.readUTF();
                        log.debug("Message: " + msg);
                        log.debug("Begin loop state: " + state);
                        Platform.runLater(() -> listView.getItems().add(msg));
                        if(state == TransferState.START_TRANSFER){
                            if(msg.equals("Ready")){
                                log.debug("Start transfer");
                                state = TransferState.INTRANSFER;
                                fis = new FileInputStream(filename);
                                filePart = readFilePart();
                                transferPart(filePart, os);
                            }
                            else{
                                log.debug("Not expected");
                                Platform.runLater(() -> listView.getItems().add("Transfer error: not expected answer"));
                                state = TransferState.WAIT_TRANSFER;
                                os.writeUTF("Break");
                            }
                        }else if(state == TransferState.INTRANSFER){
                            if(msg.equals("Receive")) {
                                log.debug("Next part");
                                filePart = readFilePart();
                                transferPart(filePart, os);
                            }
                            else if(msg.equals("Error")){
                                log.debug("Error fron server");
                                state = TransferState.WAIT_TRANSFER;
                                fis.close();
                                filePart = null;
                                partNum = 0;
                                filename = "";
                            }
                            else if(msg.equals("End")){
                                log.debug("All parts transfered");
                                state = TransferState.WAIT_TRANSFER;
                                fis.close();
                                filePart = null;
                                partNum = 0;
                                filename = "";
                            }
                            else{
                                log.debug("Not expected");
                                Platform.runLater(() -> listView.getItems().add("Transfer error: not expected answer"));
                                state = TransferState.WAIT_TRANSFER;
                                os.writeUTF("Break");
                            }
                        }
                        log.debug("End loop state: " + state);
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> listView.getItems().add("Transfer error: not expected answer"));
                    state = TransferState.WAIT_TRANSFER;
                    log.error("e=", e);
                }
            });
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            log.error("e=", e);
        }
    }
}
