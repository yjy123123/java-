import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.awt.*;
import javax.swing.JOptionPane;

public class unichat extends  myframe implements Runnable,ActionListener,KeyListener{
    private String nickname,friendname;
    private Socket socket;
    public int flag=0;
    public unichat(String nickname, String friendname, Socket socket){
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(400, 471);
        sendPane.setBounds(0, 330, 400, 95);
        showPane.setBounds(0, 0, 400, 329);
        sendArea.setLineWrap(true);
        sendArea.setWrapStyleWord(true);
        showText.setLineWrap(true);
        showText.setWrapStyleWord(true);
        showText.setEditable(false);
        sendButton.setBounds(335, 426, 60, 25);
        closeButton.setBounds(270, 426, 60, 25);
        this.remove(userPane);
        this.setTitle(friendname);
        this.nickname=nickname;
        this.friendname=friendname;
        this.socket=socket;
        //this.setVisible(true);
        sendArea.addKeyListener(this);
        sendButton.addActionListener(this);
        closeButton.addActionListener(this);
        new Thread(this).start(); //非常重要
        //new check().start();
    }
    public void run(){
        try{
        while (true) {
                InputStream is = socket.getInputStream();
                //从套接字中接收客户端信息
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(is));
                String str = br.readLine();// 读取信息
            if(str==null){
                JOptionPane.showMessageDialog(null, "对方下线");
                dispose();
                break;
            }else {
                if(!this.isVisible()&&flag==0){
                    JOptionPane.showMessageDialog(null,friendname+"给您发送了消息");
                    flag=1;
                }
                showText.append(str + "\n");// 把接收内容放到界面上
            }
        }
    } catch(Exception ex){ex.printStackTrace();
    }
}
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {//如何判断输入框是否有东西。textarea回车识别 过长不会自动换行
            e.consume();//销毁回车
            if (sendArea.getText().equals("")) {
                sendArea.setText("");
            } else {
                try {
                    OutputStream os = socket.getOutputStream();
                    PrintStream ps = new PrintStream(os);
                    showText.append(nickname + ":" + sendArea.getText()+ "\n");// 添加内容
                    ps.println(nickname + ":" + sendArea.getText());
                    sendArea.setText("");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public void keyTyped(KeyEvent e){}
    public void keyReleased(KeyEvent e){}

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==sendButton&&sendArea.getText()!=null){
            try {
                OutputStream os = socket.getOutputStream();
                PrintStream ps = new PrintStream(os);
                ps.println(nickname+":"+ sendArea.getText());
                sendArea.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else{
            if(e.getSource()==closeButton) {
                try {
                    //this.;//隐藏
                    setVisible(false);
                    flag=0;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }
    /*class check extends Thread {
        public void run() {
            try {
                while (true) {
                    if (socket.isConnected()==false) {

                    }
                }
            } catch (Exception ex) {
            }
        }


    }*/
    class close extends WindowAdapter {
        public void windowClosing(WindowEvent arg0) {
            try {
                //this.;//隐藏
                setVisible(false);
                flag=0;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
//setvisible隐藏不可用