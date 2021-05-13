import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class landing extends  JFrame implements ActionListener{
    public  String  nickname;
    public  String  account;
    public  Socket  socket;
    private JButton confirm=new JButton("确定");
    private JButton cancel=new JButton("取消");
    private JPanel  jp =new JPanel();
    private JLabel  jbnickname=new JLabel("           请输入昵称");
    private JLabel  jbaccount=new JLabel( "           请输入账号");
    private JLabel  jbpassword=new JLabel("           请输入密码");
    private JTextField jtnickname=new JTextField(15);
    private JTextField jtaccount=new JTextField(15);
    private JPasswordField jpassword=new JPasswordField(15);
    public landing(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("用户登陆");
        this.add(jp);
        jp.setSize(320,200);
        jp.setLayout(new GridLayout(4,2,10,10));
        jp.add(jbnickname);
        jp.add(jtnickname);
        jp.add(jbaccount);
        jp.add(jtaccount);
        jp.add(jbpassword);
        jp.add(jpassword);
        jp.add(confirm);
        jp.add(cancel);
        this.setSize(320,200);
        this.setVisible(true);
        cancel.addActionListener(this);
        confirm.addActionListener(this);
    }
    public void login(String password){
        try {
            socket = new Socket("127.0.0.1", 8888);
            OutputStream os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.println("登录");
            ps.println(nickname+"("+account+")");
            System.out.println(nickname+"("+account+")");
            InputStream is = socket.getInputStream();
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(is));
            String str = br.readLine();// 读
                if(str.equals("无此账号")){
                JOptionPane.showMessageDialog(this, "账号错误");
                }else{
                    if(str.equals("账号已登录")){
                        JOptionPane.showMessageDialog(this, "账号已登录");
                        System.exit(0);
                    }else {
                        if (str.equals(password)) {
                            this.dispose();//关闭登陆界面
                            new mclient(nickname, account, socket);
                        } else {
                            JOptionPane.showMessageDialog(this, "密码错误");
                        }
                    }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public void actionPerformed(ActionEvent e){
        String password=String.valueOf(jpassword.getPassword());
        if(e.getSource()==confirm){
            if(jtnickname.getText().equals("")||jtaccount.getText().equals("")||password.equals("")){
                javax.swing.JOptionPane.showMessageDialog(this, "输入框不可为空");
            }
            else{
                nickname=jtnickname.getText();
                account=jtaccount.getText();
                login(password);

            }
        }
        else{
            System.exit(0);
        }
    }

}
