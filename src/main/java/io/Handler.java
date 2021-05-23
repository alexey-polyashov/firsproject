package io;

import java.io.*;
import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Handler implements Runnable, Closeable {

    private final Socket socket;
    private TransferState state = TransferState.WAIT_TRANSFER;

    private FileOutputStream fos;

    public Handler(Socket socket) {
        this.socket = socket;
    }

    private String filePartReceive(FilePart fp) throws IOException {
        String msg;
        fos.write(fp.getData());
        fos.flush();
        log.debug("File part flushed: " + fp.getPart());
        msg = "Receive";
        if(fp.isFinish()){
            log.debug("End of file");
            fos.close();
            state=TransferState.WAIT_TRANSFER;
            log.debug("File closed");
            msg = "End";
        }
        return msg;
    }

    @Override
    public void run() {
        try (DataInputStream is = new DataInputStream(socket.getInputStream());
             DataOutputStream os = new DataOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(is)) {

            while (true) {
                String msg ="";
                if(state == TransferState.WAIT_TRANSFER) {
                    msg = is.readUTF();
                    log.debug("received: {}", msg);
                    if(msg.equals("StartFileTransfer")){
                        log.debug("Transfer request receive");
                        state = TransferState.START_TRANSFER;
                        msg = "Ready";
                    }
                }else if(state == TransferState.START_TRANSFER){
                    try{
                        log.debug("Read start part");
                        FilePart fp = (FilePart)ois.readObject();
                        String filename = fp.getFilename();
                        String[] nameParts = filename.split("\\.(?=[^\\.]+$)");
                        filename = nameParts[0]+"_srv." + nameParts[1];
                        log.debug("File name: " + filename);
                        fos = new FileOutputStream(filename);
                        msg = filePartReceive(fp);
                        state = TransferState.INTRANSFER;
                    }catch(Exception e){
                        log.error("e=", e);
                        msg = "Error";
                        state = TransferState.WAIT_TRANSFER;
                    };
                }else if(state == TransferState.INTRANSFER){
                    try{
                        log.debug("Read next part");
                        FilePart fp = (FilePart)ois.readObject();
                        msg = filePartReceive(fp);
                    }catch(Exception e){
                        log.error("e=", e);
                        msg = "Error";
                        state = TransferState.WAIT_TRANSFER;
                    };
                }
                log.debug("Outer message: " + msg);
                log.debug("State: " + state);
                os.writeUTF(msg);
            }
        } catch (Exception e) {
            log.error("e=", e);
            state = TransferState.WAIT_TRANSFER;
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}