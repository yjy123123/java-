import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;
public class server  extends myframe implements ActionListener,KeyListener, Runnable{
    private Socket socket = null;
    private ServerSocket serversocket = null;
    private ArrayList<Socket> socketList=new ArrayList<>();
    private HashMap clientlist=new HashMap();
    private HashMap accountmanage=new HashMap();//账号管理
        //private static final long serialVersionUID = 1L;  //解决办法
    public server() throws Exception{
        this.setTitle("服务器端");
        userlist.setBorder(BorderFactory.createTitledBorder("  在线用户列表     "));
        this.remove(closeButton);
        sendArea.addKeyListener(this);
        sendButton.addActionListener(this);
        this.setVisible(true);
        this.setResizable(false);  //不能自己决定窗口大小，固定
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serversocket = new ServerSocket(8888);//服务器端监听端口，等待连接
        new Thread(this).start();
        }

    public void run() {
        try {
            while (true) {
                socket = serversocket.accept();
                InputStream is = socket.getInputStream();
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(is));
                PrintStream ps=new PrintStream(socket.getOutputStream());
                String str = br.readLine();// 读
                if(str.equals("登录")){
                    str=br.readLine();
                    if(clientlist.containsKey(str)){
                        ps.println("账号已登录");
                    }else{
                        if(!accountmanage.containsKey(str)){
                            ps.println("无此账号");
                        }else{
                            ps.println(accountmanage.get(str));

                            Thread.sleep(200);
                            //实现序列化
                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                            //读好友列表
                            oos.writeObject(clientlist);    //序列化，但出现错误，通过查阅资料，发现hashmap有另一个类（socket），传输会出错
                            oos.flush();  //缓冲流
                            ChatThread ct = new ChatThread(socket);
                            ct.start();
                            socketList.add(socket);       //将连接的客户端套接字储存在数组中存放
                            //showText.append(socket.getInetAddress().getHostAddress() +"连接成功！" + "\n");
                            //为每一个客户端开启线程，便于发送消息
                        }
                    }
                }else{
                    if(str.equals("注册")){
                        str=br.readLine();
                        String[] temp=str.split("#");
                        int account=(int) (Math.random()*10000);
                        accountmanage.put(temp[0]+"("+account+")",temp[1]);
                        ps.println(account);
                        socket.close();
                    }
                }

            }
        }catch(Exception ex){
                ex.printStackTrace();
            }
        }


    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {//如何判断输入框是否有东西。textarea回车识别 过长不会自动换行
            e.consume();//销毁回车
            if (sendArea.getText().equals("")) {
                sendArea.setText("");
            } else {
                try {
                    for (Socket clientsocket : socketList) { //将 str 转发给所有客户端
                        OutputStream os = clientsocket.getOutputStream();
                        PrintStream ps = new PrintStream(os);
                        ps.println("系统消息:"+ sendArea.getText()); //向某一客户端的套接字发送信息
                    }
                    showText.append("系统消息:"+ sendArea.getText()+'\n');
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
        if(e.getSource()==sendButton&&sendArea.getText()!=""){
            try {
                for (Socket clientsocket : socketList) { //将 str 转发给所有客户端
                    OutputStream os = clientsocket.getOutputStream();
                    PrintStream ps = new PrintStream(os);
                    ps.println("系统消息:"+ sendArea.getText()); //向某一客户端的套接字发送信息
                }
                showText.append("系统消息:"+ sendArea.getText()+'\n');
                sendArea.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public void mouseClicked(MouseEvent e){
        if(e.getClickCount() == 2&&!userlist.isSelectionEmpty()){
            int a=JOptionPane.showConfirmDialog(this,"您确认踢出改群员吗？","确认",JOptionPane.YES_NO_OPTION);
        if(a==JOptionPane.YES_OPTION){
            int index = userlist.getSelectedIndex(); //获取选择项

            try {
                for (Socket clientsocket : socketList) { //将 str 转发给所有客户端
                    OutputStream os = clientsocket.getOutputStream();
                    PrintStream ps = new PrintStream(os);
                    ps.println(userlist.getSelectedValue()+"#"+"系统消息:" +userlist.getSelectedValue()+"被管理员移除，退出聊天室————————"); //向某一客户端的套接字发送信息
                }
                showText.append("系统消息:" +userlist.getSelectedValue()+"被管理员移除，退出聊天室————————"+'\n');
            }catch (Exception ex){
                ex.printStackTrace();
            }
            //对应用户收到消息退出，解除socket绑定
            clientlist.remove(userlist.getSelectedValue());
            dlm.remove(index);
            userlist.setModel(dlm);

        }
        }
    }
    class ChatThread extends Thread {   //负责客户端的Socket信息的接收和发送，开启线程
        private Socket socket = null;
        private BufferedReader br = null;
        public PrintStream ps = null;
        public ChatThread(Socket socket) throws Exception {
            this.socket = socket;
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ps = new PrintStream(socket.getOutputStream());
        }

        public void run() {
            try {
                while (true) {
                    String rMsg =br.readLine();       //读取该某一套接字传来的信息，加上传送方的ip
                    if(rMsg.endsWith("加入多人聊天————————"))
                    {   int flag=0;
                        String[] tmp=rMsg.split("#");
                        Set<String> key = clientlist.keySet();
                        for(String friend:key){
                            if(friend.equals(tmp[0])){
                                ChatThread chatthread = new ChatThread(socket);
                                chatthread.ps.println("重复登录");
                                flag=1;
                            }
                        }
                        if(flag==1){
                            break;
                        }
                        clientlist.put(tmp[0],socket.getInetAddress().getHostAddress()+"#"+tmp[1]);
                        dlm.addElement(tmp[0]);
                        userlist.setModel(dlm);
                        rMsg=tmp[2];
                        for (Socket clientsocket : socketList) { //将 str 转发给所有客户端
                            ChatThread chatthread = new ChatThread(clientsocket);
                            chatthread.ps.println(tmp[0]+"#"+socket.getInetAddress().getHostAddress() +"#"+tmp[1]+"#"+tmp[2]);       //向某一客户端的套接字发送信息
                        }
                    }
                    else{

                        if(rMsg.endsWith("退出聊天室————————")){
                            String[] tmp=rMsg.split("#");
                            clientlist.remove(tmp[0]);//利用昵称来判断上下线
                            dlm.removeElement(tmp[0]);
                            userlist.setModel(dlm);

                            for (Socket clientsocket : socketList) { //将 str 转发给所有客户端
                                ChatThread chatthread = new ChatThread(clientsocket);
                                chatthread.ps.println(rMsg);       //向某一客户端的套接字发送信息
                            }
                            rMsg=tmp[1];

                        }
                        else{
                            for (Socket clientsocket : socketList) { //将 str 转发给所有客户端
                                ChatThread chatthread = new ChatThread(clientsocket);
                                chatthread.ps.println(rMsg);       //向某一客户端的套接字发送信息
                        }
                    }

                    }
                    showText.append(rMsg+'\n');
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws Exception{
        server  server=new server();
    }
}

/*public class server extends JFrame {
    private Socket socket = null;
    private ServerSocket serversocket = null;
    private JTextArea taMsg =new JTextArea();
    private ArrayList<Socket> socketList=new ArrayList<>();
    public server() throws Exception{
        this.setTitle("服务器端");
        this.add(taMsg,BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,400);
        this.setVisible(true);
        serversocket = new ServerSocket(8888);//服务器端监听端口，等待连接
        try{
            while(true){
                socket = serversocket.accept();
                taMsg.append(socket.getInetAddress().getHostAddress() +
                        "连接成功！"+"\n");
                socketList.add(socket);       //将连接的客户端套接字储存在数组中存放
                ChatThread ct = new ChatThread(socket);
                //为每一个客户端开启线程，便于发送消息
                ct.start();
            }
        }catch(Exception ex){
            ex.printStackTrace();}
    }
    class ChatThread extends Thread{   //负责客户端的Socket信息的接收和发送，开启线程
        private Socket socket = null;
        private BufferedReader br = null;
        public PrintStream ps = null;
        public ChatThread(Socket socket) throws Exception {
            this.socket = socket;
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ps = new PrintStream(socket.getOutputStream());
        }
        public void run(){
            try{
                while(true){
                    String rMsg =br.readLine();       //读取该某一套接字传来的信息，加上传送方的ip
                    for(Socket clientsocket : socketList){ //将 str 转发给所有客户端
                        ChatThread chatthread = new ChatThread(clientsocket);
                        chatthread.ps.println(rMsg);       //向某一客户端的套接字发送信息
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();}
        }
    }

    public static void main(String[] args) throws Exception{
        server  server=new server();
    }
}*/
        //ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
//ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());



