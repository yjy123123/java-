import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javax.swing.*;
public class mclient extends myframe implements ActionListener,KeyListener, Runnable {

    private Socket socket = null;
    public String nickname, account;
    public int portnum;
    private HashMap friendlist;
     private HashMap unichatMap=new HashMap<>();
    //private static final long serialVersionUID = 1L;
    private ServerSocket serversocket = null;
    private Socket unisocket=null;
    //private ServerSocket serverunisocket=null;

    public mclient(String nickname, String account,Socket socket) {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new close());
        this.nickname = nickname;
        this.account = account;
        this.socket=socket;
        this.setTitle("多人聊天（" + nickname + '）');
        sendArea.addKeyListener(this);
        sendButton.addActionListener(this);
        closeButton.addActionListener(this);
        this.setResizable(false);  //不能自己决定窗口大小，固定
        this.setVisible(true);
        try {
            PrintStream ps =
                    new PrintStream(socket.getOutputStream());
            //实现序列化
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());//读好友列表
            friendlist = (HashMap) ois.readObject();
            portnum = (int) (Math.random() * 1000 + 10000);
            Set<String> key = friendlist.keySet();
            for (String friend : key) {
                dlm.addElement(friend);
                userlist.setModel(dlm);
            }
            String str = nickname + "(" + account + ")" + "#" + portnum + "#" + "————————" + nickname + "加入多人聊天————————";
            ps.flush();
            ps.println(str);
            new Thread(this).start();
            serversocket = new ServerSocket(portnum);
             new receive().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*class MyCellRenderer extends JLabel implements ListCellRenderer {
        public Component getListCellRendererComponent(
                JList list,              // the list
                Object value,            // value to display
                int index,               // cell index
                boolean isSelected,      // is the cell selected
                boolean cellHasFocus)    // does the cell have focus
        {

            if(unichatMap.containsKey(value)) {
                setBackground(Color.pink);
                }
            setOpaque(true);
            return this;
        }
    }*/

 //myList.setCellRenderer(new MyCellRenderer());


    public void run() {
        try {
            while (true) {
                InputStream is = socket.getInputStream();
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(is));
                String str = br.readLine();// 读
                if(str.endsWith("系统消息:" +nickname+"("+account+")"+"被管理员移除，退出聊天室————————")) {
                    JOptionPane.showMessageDialog(this,"系统消息:您被管理员移除，退出聊天室————————");
                    socket.close();
                    System.exit(0);//关闭登陆界面
                }
                 else{
                    if(str.endsWith("退出聊天室————————")){
                        String tmp[]=str.split("#");
                        friendlist.remove(tmp[0]);
                        dlm.removeElement(tmp[0]);
                        userlist.setModel(dlm);
                        showText.append(tmp[1]+"\n");// 添加内容
                    }
                    else {
                        if (str.endsWith("加入多人聊天————————")) {
                            String tmp[] = str.split("#");
                            friendlist.put(tmp[0], tmp[1] + "#" + tmp[2]);//nickname  adress portnum
                            dlm.addElement(tmp[0]);
                            userlist.setModel(dlm);
                            showText.append(tmp[3] + "\n");
                        } else {
                                showText.append(str + "\n");// 添加内容
                            }
                        }
                    }
                 }
        } catch (Exception e) {
            e.printStackTrace();
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
                    ps.println(nickname + ":" + sendArea.getText());
                    sendArea.setText("");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public void keyTyped(KeyEvent arg0){}
    public void keyReleased(KeyEvent arg0){}


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
                    OutputStream os = socket.getOutputStream();
                    PrintStream ps = new PrintStream(os);
                    ps.println(nickname+"("+account+")"+"#"+"————————" + nickname + "退出聊天室————————" );
                    System.exit(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }
    public void mouseClicked(MouseEvent e){
       if(e.getClickCount() == 2){
            if(userlist.getSelectedValue().equals(nickname+"("+account+")")){
                //点击自己什么都不做
            }
            else{
                if(userlist.getSelectionBackground()==Color.pink||unichatMap.containsKey(userlist.getSelectedValue())){
                    //点击发送信息给自己的人，窗口可视化
                    ((unichat) unichatMap.get(userlist.getSelectedValue())).setVisible(true);  //显示窗口
                    //userlist.setCellRenderer(new MyCellRenderer());

                    //getListCellRendererComponent(userlist,userlist.getSelectedValue(),userlist.getSelectedIndex(),true,true);//变色

                }
                else{
                    try {

                        String tmp[] = ((String) friendlist.get(userlist.getSelectedValue())).split("#");
                        unisocket = new Socket(tmp[0], Integer.valueOf(tmp[1]));
                        unichat uc=new unichat(nickname,(String)userlist.getSelectedValue(),unisocket);
                          //SwingUtilities.invokeLater(uc);//swing线程不安全
                        unichatMap.put(userlist.getSelectedValue(),uc);
                        uc.setVisible(true);
                        OutputStream os = unisocket.getOutputStream();
                        PrintStream ps = new PrintStream(os);
                        ps.println(nickname +"("+account+")");
                       }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    }
            }

        }
    }

    class  receive extends Thread {
        public void run() {
            try {
                while (true) {
                    unisocket = serversocket.accept();
                    InputStream is = unisocket.getInputStream();
                    BufferedReader br =
                            new BufferedReader(new InputStreamReader(is));
                    String msg = br.readLine();// 读
                    //String tmp[] = msg.split("#");
                    // int index = dlm.indexOf(tmp[0]);
                    //int index = dlm.indexOf(msg);
                    //userlist.setCellRenderer(new MyCellRenderer());
                    //userlist.repaint();
                    //getListCellRendererComponent(userlist,msg,index,false,true);//变色
                    unichat uc = new unichat(nickname, msg, unisocket);
                    unichatMap.put(msg, uc);
                    //JOptionPane.showMessageDialog(null,msg+"给您发送了消息");
                    //SwingUtilities.invokeLater(uc);//Swing 不是线程安全的。


                }
            } catch (Exception ex) {

            }
        }

    }
    class close extends WindowAdapter {
        public void windowClosing(WindowEvent arg0) {
            int a = JOptionPane.showConfirmDialog(null, "您确认关闭吗？", "确认", JOptionPane.YES_NO_OPTION);
            if (a == JOptionPane.YES_OPTION) {
                try {
                    OutputStream os = socket.getOutputStream();
                    PrintStream ps = new PrintStream(os);
                    ps.println(nickname + "(" + account + ")" + "#" + "————————" + nickname + "退出聊天室————————");
                    System.exit(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

}