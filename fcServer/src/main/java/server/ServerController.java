package server;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ServerController {

    @FXML
    public Label serverState;
    MainServer mainServer;

    public void start(ActionEvent actionEvent) throws InterruptedException {

        Thread tr1 = new Thread(()->{
            mainServer = new MainServer();
            mainServer.start();
        });

        tr1.setDaemon(true);
        tr1.start();

        Thread.sleep(1500);

        if(mainServer.getChannelFuture().channel().isActive()) {
            serverState.setText("Server running");
        }


    }

    public void stop(ActionEvent actionEvent) {

        if(mainServer!=null){
            mainServer.stop();
            mainServer = null;
        }
        serverState.setText("Server stopped");

    }
}
