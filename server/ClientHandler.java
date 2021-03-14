package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.rmi.ConnectIOException;

public class ClientHandler {

    private ServerClass myServer;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(ServerClass myServer, Socket socket) {

        try {
            this.myServer = myServer;
            this.socket = socket;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());

            this.name = "";

            Thread thr1 = new Thread(()->{
               try{
                   authentication();
                   if(!socket.isClosed()) {
                       readMessages();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
                   throw new RuntimeException("Проблемы при создании обработчика клиента");
               }finally {
                   closeConnection();
               }
            });
            thr1.setDaemon(true);
            thr1.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void authentication() throws IOException {
        try{
            while(true){
                String str = dis.readUTF();
                if(str.startsWith("/auth")){
                    String[] parts = str.split("\\s");
                    String nick = myServer.getAuthService().getNickByLogin(parts[1], parts[2]);
                    System.out.println("nick is - " + nick);
                    if(nick != null){
                        if(!myServer.isNickBusy(nick)){
                            name = nick;
                            sendMsg("/authok " + nick);
                            myServer.broadcastMsg(name + " зашел в чат");
                            myServer.subscribe(this);
                            return;
                        }else{
                            sendMsg("Учетная запись уже используется");
                        }
                    }else{
                        sendMsg("Неверный логин/пароль");
                    }
                }
            }
        }catch (EOFException connExpt){
            try{
                dis.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("client disconnected");
        }
    }

    public void readMessages() throws IOException{
        while (true){
            String strFromClient = dis.readUTF();
            if(strFromClient.equals("/end")){
                return;
            }
            if(strFromClient.startsWith("/w")){
                String[] parts = strFromClient.split("\\s", 3);
                if(!myServer.senMessageToClient(parts[2], parts[1])){
                    sendMsg(parts[1] + " не в сети");
                }
            }else {
                myServer.broadcastMsg(name + ": " + strFromClient);
            }
        }
    }

    public void sendMsg(String msg){
        try{
            dos.writeUTF(msg);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + " вышел из чата");
        try{
            dis.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
