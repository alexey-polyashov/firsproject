package server;

import java.io.IOException;

public class MainServer {

    public static void main(String[] args) {

        try{
            ServerClass myServer = new ServerClass(8181);
            myServer.start();
            myServer.listenPort();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
