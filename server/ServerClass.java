package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClass {

    private ServerSocket sSocket;
    private Socket clientSocket;

    private DataInputStream dis = null;
    private DataOutputStream dos = null;


    public ServerClass(int port) throws IOException {

        sSocket = new ServerSocket(port);

    }

    public void start(){

        try {
            System.out.println("Start server");
            clientSocket = sSocket.accept();
            System.out.println("Client is connected");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void closeConnection(DataOutputStream dos, DataInputStream dis){
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listenPort(){

        try {
            dis = new DataInputStream(clientSocket.getInputStream());
            dos = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Thread thr = new Thread(()-> {
            while (true) {

                try {
                    String inMessage = dis.readUTF();
                    System.out.println("Received message: " + inMessage);
                    if (inMessage.equalsIgnoreCase("/exit")) {
                        dos.writeUTF("/exit");
                        dos.flush();
                        System.out.println("Server is disconnected");
                        break;
                    } else {
                        dos.writeUTF("EHO: " + inMessage);
                        dos.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            closeConnection(dos, dis);
        }
        );
        thr.start();

        Thread thr2 = new Thread(()->{
            while(true){
                try {
                    if(br.ready()){
                        String sb = br.readLine();
                        dos.writeUTF("SERVER: " + sb);
                        dos.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thr2.setDaemon(true);
        thr2.start();

    }


}
