import javax.swing.*;
import java.awt.*;
import java.util.*;
public class gui extends JFrame {
    JTextArea sendArea = new JTextArea();
    private JLabel  jbnickname=new JLabel( "Hello World !",JLabel.CENTER);
    JButton closeButton = new JButton("test");
    private JPanel  jp =new JPanel();
    public gui(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("人机交互");
        this.add(jp);
        jp.setSize(320,200);
        jp.setLayout(null);

        jbnickname.setBounds(100,50,100,30);

        closeButton.setBounds(100,100,100,30);
        jp.add(jbnickname);
        jp.add(closeButton);
        this.setSize(320,200);
        this.setVisible(true);

    }
}
