package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatWindow extends JFrame {

    private final int PORT_NUMBER = 8181;
    private final String SERVER_NAME = "localhost";
    private JTextArea historyPanel;
    private JTextField textPanel;
    private JPanel panel;

    private Socket chatSocket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private boolean authorized = false;

    public void openConnection(JFrame mw) throws IOException {

        chatSocket = new Socket(SERVER_NAME, PORT_NUMBER);
        dis = new DataInputStream(chatSocket.getInputStream());
        dos = new DataOutputStream(chatSocket.getOutputStream());

        setAuthorized(false);

        Thread thr2 = new Thread(()->{
            try {
                String inMessage = null;
                while(true){
                    inMessage = dis.readUTF();
                    if(inMessage.startsWith("/authok")){
                        setAuthorized(true);
                        String[] parts = inMessage.split("\\s", 2);
                        mw.setTitle(parts[1]);
                        historyPanel.append(inMessage + "\n");
                        break;
                    }
                    historyPanel.append(inMessage + "\n");
                }
                while(true){
                    if(chatSocket.isClosed()){
                        break;
                    }
                    if(!chatSocket.isConnected()){
                        break;
                    }
                    inMessage = dis.readUTF();
                    if(inMessage.equalsIgnoreCase("/end")){
                        break;
                    }
                    historyPanel.append(inMessage + "\n");
                }
                closeConnection(dos, dis, chatSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thr2.setDaemon(true);
        thr2.start();

    }

    public void closeConnection(DataOutputStream dos, DataInputStream dis, Socket clientSocket){
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

    public void setAuthorized(boolean b){
        this.authorized = b;
    }

    public void sendMessage(String text){

        try {
            if(!text.trim().isEmpty()) {
                textPanel.setText("");
                if(text.startsWith("/w")){
                    String[] s = text.split("\\s", 3);
                    historyPanel.append("TO " + s[1] + ":" + s[2]);
                    historyPanel.append("\n");
                }else{
                    historyPanel.append(text);
                    historyPanel.append("\n");
                }
                dos.writeUTF(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
        }

    }

    public ChatWindow(){
        try {
            openConnection(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        prepareGUI();
    }

    public void prepareGUI(){

        setTitle("Неавторизованный пользователь");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        textPanel = new JTextField();
        historyPanel = new JTextArea();
        JScrollPane scroll = new JScrollPane(historyPanel);

        historyPanel.setBackground(Color.LIGHT_GRAY);

        textPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(textPanel, BorderLayout.SOUTH);

        textPanel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hs = textPanel.getText();
                sendMessage(hs);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            try {
                dos.writeUTF("/end");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            }
        });

        add(panel);

        setSize(500, 500);
        setVisible(true);

    }

}
