import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class register extends  JFrame implements ActionListener {
    public  String  nickname;
    public  String  account;
    public  Socket  socket;
    private JButton confirm=new JButton("注册");
    private JButton cancel=new JButton("取消");
    private JPanel  jp =new JPanel();
    private JLabel  jbnickname=new JLabel( "           请输入昵称");
    private JLabel  jbpassword=new JLabel( "           请输入密码");
    private JLabel  jbconfirm=new JLabel("           请确认密码");
    private JTextField jnickname=new JTextField(15);
    private JPasswordField jpassword=new JPasswordField(15);
    private JPasswordField jconfirm=new JPasswordField(15);
    public register(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("用户注册");
        this.add(jp);
        jp.setSize(320,200);
        jp.setLayout(new GridLayout(4,2,10,10));
        jp.add(jbnickname);
        jp.add(jnickname);
        jp.add(jbpassword);
        jp.add(jpassword);
        jp.add(jbconfirm);
        jp.add(jconfirm);
        jp.add(confirm);
        jp.add(cancel);
        this.setSize(320,200);
        this.setVisible(true);
        cancel.addActionListener(this);
        confirm.addActionListener(this);
    }
    public void signup(String nickname,String password) throws Exception{
        socket=new Socket("127.0.0.1",8888);

        OutputStream os = socket.getOutputStream();
        PrintStream ps = new PrintStream(os);
        ps.println("注册");
        ps.println(nickname+"#"+password);
        InputStream is = socket.getInputStream();
        BufferedReader br =
                new BufferedReader(new InputStreamReader(is));
        String account = br.readLine();// 读
        JOptionPane.showMessageDialog(this,"注册成功！"+'\n'+"您的账号："+account);

    }


    public void actionPerformed(ActionEvent e){
        if(e.getSource()==confirm){
            String password1 = String.valueOf(jpassword.getPassword());
            String password2 = String.valueOf(jconfirm.getPassword());
            if(password1.equals("")||password2.equals("")||jnickname.getText().equals("")){
                javax.swing.JOptionPane.showMessageDialog(this, "输入框不可为空");
            }
            else {
                if (password1.equals(password2)) {
                    try {
                        signup(jnickname.getText(), password1);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    new start();
                    this.dispose();
                }else {
                    //if (!(jconfirm.getPassword().toString().equals(jpassword.getPassword().toString()))) {
                        JOptionPane.showMessageDialog(this, "两次密码不同");
                    //}
                }
            }
        }
        else{
            new start();
        }
    }
}
