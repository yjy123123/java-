import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.*;
import java.util.*;
public class myframe extends JFrame  implements MouseListener{
     JTextArea sendArea = new JTextArea();
     JScrollPane sendPane = new JScrollPane(sendArea);
     JTextArea showText = new JTextArea();
     JScrollPane showPane = new JScrollPane(showText);//可滚动
     JButton sendButton = new JButton("发送");
     JButton closeButton = new JButton("关闭");
     DefaultListModel dlm = new DefaultListModel();
     JList   userlist=new JList();

    JScrollPane userPane = new JScrollPane(userlist);//可滚动
    public myframe() {
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(500, 471);
        sendPane.setBounds(0, 330, 340, 95);
        showPane.setBounds(0, 0, 340, 329);
        sendArea.setLineWrap(true);
        sendArea.setWrapStyleWord(true);
        showText.setLineWrap(true);
        showText.setWrapStyleWord(true);
        showText.setEditable(false);
        sendButton.setBounds(280, 426, 60, 25);
        closeButton.setBounds(210, 426, 60, 25);
        //closeButton.setBounds(210, 426, 60, 25);

        userPane.setBounds(341,0,158,469);
        userlist.setSize(158,469);
        userlist.setBorder(BorderFactory.createTitledBorder("  好友列表     "));
        userlist.addMouseListener(this);
        userlist.setFixedCellHeight(30);
        this.add(sendPane);
        this.add(showPane);
        this.add(sendButton);
        this.add(userPane);
        this.add(closeButton);
        //add(closeButton);
        //this.setVisible(true);
        //this.setFocusable(true);
    }
    public void mouseClicked(MouseEvent e){

    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}


}