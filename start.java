import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class start extends  JFrame implements ActionListener {
    private JButton signup=new JButton("注册");
    private JButton login=new JButton("登陆");
    private JButton cancel=new JButton("取消");
    public start(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("聊天软件");
        this.setLayout(null);
        signup.setBounds(120,20,100,30);
        login.setBounds(120,70,100,30);
        cancel.setBounds(120,120,100,30);
        this.setSize(320,200);
        this.add(signup);
        this.add(login);
        this.add(cancel);
        this.setSize(320,200);
        this.setVisible(true);
        cancel.addActionListener(this);
        signup.addActionListener(this);
        login.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==signup){
            new register();
            this.dispose();//关闭登陆界面
            }
        else{
            if (e.getSource()==login){
                new landing();
                this.dispose();//关闭登陆界面
            }
            else {
                System.exit(0);
            }
        }
    }
    public static void main(String args[]){
        start s=new start();
    }
}
