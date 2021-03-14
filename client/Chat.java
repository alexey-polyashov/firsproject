package client;

import javax.swing.*;

public class Chat {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new ChatWindow();
        });
    }

}
