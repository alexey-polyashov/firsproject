package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerClass {

    private final int PORT = 8181;

    private AuthService authService;
    private List<ClientHandler> clients;

    public void listenPort(){

        System.out.println("Start server");

        try(ServerSocket sSocket = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            while(true) {
                System.out.println("Server wait connection");
                Socket clientSocket = sSocket.accept();
                new ClientHandler(this, clientSocket);
                System.out.println("New client connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(authService != null){
                authService.stop();
            }
        }

    }

    public synchronized boolean isNickBusy(String nick){
        for(ClientHandler o: clients){
            if(o.getName().equals(nick)){
                return true;
            }
        }
        return false;
    }

    public synchronized void subscribe(ClientHandler o){
        clients.add(o);
    }

    public synchronized void unsubscribe(ClientHandler o){
        clients.remove(o);
    }

    public AuthService getAuthService(){
        return authService;
    }

    public synchronized void broadcastMsg(String msg){
        for (ClientHandler o: clients){
            o.sendMsg(msg);
        }
    }

    public synchronized boolean senMessageToClient(String msg, String nick){
        for (ClientHandler o: clients){
            if(o.getName().equalsIgnoreCase(nick)) {
                o.sendMsg(msg);
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastClientsList() {
        StringBuilder sb = new StringBuilder("/clients ");
        for (ClientHandler o : clients) {
            sb.append(o.getName() + " ");
        }
        broadcastMsg(sb.toString());
    }


}
