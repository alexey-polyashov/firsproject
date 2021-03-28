package chat.server;

import chat.common.InitParameters;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {

    InitParameters common;
    private AuthService authService;
    private DBInterface dbService;
    private boolean isLogin = false;
    private Socket clientSocket;
    volatile private boolean timeOut;
    UserInfo user;
    DataInputStream dis;
    DataOutputStream dos;
    volatile private boolean isAuthorized;

    public ClientHandler(Socket clientSocket, DBInterface dbService) {
        this.clientSocket = clientSocket;
        this.dbService = dbService;
        startSession();
    }

    public void startSession(){

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Thread mainThread = new Thread(()->{
            try {
                authService = new AuthService(dbService);
                try {
                    dis = new DataInputStream(clientSocket.getInputStream());
                    dos = new DataOutputStream(clientSocket.getOutputStream());
                    authentication();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(isLogin) {
                    readMessages();
                }
            } catch (IOException e) {
                if(timeOut){
                    System.out.println("login time out");
                }else {
                    e.printStackTrace();
                }
            } finally {
                close();
            }
        });
        mainThread.setDaemon(true);

        executorService.execute(mainThread);
        executorService.shutdown();

    }

    private void authentication() throws IOException {
        while (true){
            String str = dis.readUTF();
            if(str.startsWith("/auth")){
                String[] parts = str.split("\\s");
                System.out.println("Try to login - " + parts[1] + "; " + parts[2]);
                user = authService.loginUser(parts[1], parts[2]);
                if(user != null){
                    if(!Server.isNickBusy(user.getName())){
                        isLogin = true;
                        System.out.println("login success");
                        Server.subscribe(this);
                        sendMsg("/authok " + user.getNick());
                        Server.broadcastMsg(user.getNick() + " зашел в чат");
                        return;
                    }else{
                        System.out.println("nick is used");
                        sendMsg("/nickisused");
                    }
                }else{
                    System.out.println("wrong password");
                    sendMsg("/wrongpass");
                    break;
                }
            }
        }
    }

    public void readMessages() throws IOException{
        while (true){
            String strFromClient = dis.readUTF();
            System.out.println("get from chat.client - " + strFromClient);
            if(strFromClient.equals("/end")){
                Server.unsubscribe(this);
                return;
            }
            else if(strFromClient.startsWith("/changenick")){
                String[] parts = strFromClient.split("\\s", 2);
                System.out.println("try change nick - " + parts[1]);
                String oldNick = user.getNick();
                try {
                    UserInfo newUser = authService.changeNick(user.getName(), parts[1]);
                    if(newUser!=null){
                        System.out.println("nick changed ('" + oldNick + "'->'" + newUser.getNick() + "')");
                        Server.senMessageToClient("/newnick " + newUser.getNick(), oldNick);
                        user = newUser;
                    }
                } catch (NickIsBusy nickIsBusy) {
                    System.out.println("nick busy");
                    Server.senMessageToClient("/nickbusy", user.getNick());
                } catch (Exception throwables) {
                    System.out.println("base error");
                    Server.senMessageToClient("/baseerror", user.getNick());
                }
            }
            else if(strFromClient.startsWith("/w")){
                String[] parts = strFromClient.split("\\s", 3);
                if(!Server.senMessageToClient(parts[2], parts[1])){
                    sendMsg(parts[1] + " не в сети");
                }
            }else {
                Server.broadcastMsg(user.getNick() + ": " + strFromClient);
            }
        }
    }

    private void close(){
        System.out.println("Client is closed");
        if(dis!=null){
            try {
                dis.close();
                dis = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(dos!=null){
            try {
                dos.close();
                dos = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(clientSocket!=null && !clientSocket.isClosed()){
            try {
                clientSocket.close();
                clientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Server.unsubscribe(this);
    }

    public void sendMsg(String msg){
        try{
            dos.writeUTF(msg);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String getName() {
        if (user != null) {
            return user.getName();
        } else {
            return "";
        }
    }

    public String getNick(){
        if(user!=null) {
            return user.getNick();
        }else{
            return "";
        }
    }



}
