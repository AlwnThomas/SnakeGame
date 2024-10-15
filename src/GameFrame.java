import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {

    GameFrame(){

        GamePanel gamePanel;
        this.add(gamePanel = new GamePanel());

        JPanel border = new JPanel();
        border.setBackground(new Color(99,136,64));
        border.setLayout(new BorderLayout());
        border.add(gamePanel, BorderLayout.CENTER);
        border.setBorder(BorderFactory.createEmptyBorder(25,15,15,15));

        this.add(border);
        this.setTitle("Snake");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

    }
}
