import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow extends JFrame {

    public ChatWindow(){

        setTitle("Имитация чата");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextField textPanel = new JTextField();

        JTextArea historyPanel = new JTextArea();
        JScrollPane scroll = new JScrollPane(historyPanel);

        historyPanel.setBackground(Color.LIGHT_GRAY);

        textPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(textPanel, BorderLayout.SOUTH);

        textPanel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hs = historyPanel.getText();
                hs += textPanel.getText() + "\n";
                textPanel.setText("");
                historyPanel.setText(hs);
            }
        });

        add(panel);

        setSize(500, 500);
        setVisible(true);

    }

}
