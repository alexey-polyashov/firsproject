package server;

import common.InitParameters;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server{

    static InitParameters common;
    private static List<ClientHandler> clients;
    private static DBService dbService = new DBService();

    public static void main(String[] args) {

        System.out.println("Server start");

        while(true) {
            try (ServerSocket sSocket = new ServerSocket(common.PORT)) {
                clients = new ArrayList<>();
                while (true) {
                    System.out.println("Server wait connection");
                    Socket clientSocket = sSocket.accept();
                    new ClientHandler(clientSocket, dbService);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }finally {
                dbService.close();
            }
        }

    }

    public static synchronized boolean isNickBusy(String nick){
        for(ClientHandler o: clients){
            if(o.getName().equals(nick)){
                return true;
            }
        }
        return false;
    }

    public static synchronized void subscribe(ClientHandler o){
        clients.add(o);
    }

    public static synchronized void unsubscribe(ClientHandler o){
        clients.remove(o);
    }

    public static synchronized void broadcastMsg(String msg){
        for (ClientHandler o: clients){
            o.sendMsg(msg);
        }
    }

    public static synchronized boolean senMessageToClient(String msg, String nick){
        for (ClientHandler o: clients){
            if(o.getNick().equalsIgnoreCase(nick)) {
                o.sendMsg(msg);
                return true;
            }
        }
        return false;
    }


}
