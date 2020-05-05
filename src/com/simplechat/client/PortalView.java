package com.simplechat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import com.simplechat.client.RegisterView;

public class PortalView extends JFrame implements
        ActionListener {

    private JTextField tfName;      // 用户名输入框
    private JTextField tfPassword;   // 密码输入框
    private JButton btnLogin;        // 登录按钮
    private JButton btnRegister;        // 注册按钮

    private JLabel labelName;
    private JLabel labelPassword;
    private JPanel jp1, jp2, jp3;

    private JLabel labelHost;
    private JLabel labelPort;
    private JTextField tfHost;      // 服务器地址输入框
    private JTextField tfPort;      // 服务器端口输入框
    private JButton btnConnect;     // 连接/断开服务器按钮

    /**
     * 处理图形化界面
     * 进入门户，先连接，再选择登录或注册
     */
    //用户界面初始化
    private void initView() {
        jp1 = new JPanel();
        labelHost = new JLabel("主机地址");
        tfHost = new JTextField(15);
        tfHost.setText("localhost");
        labelPort = new JLabel("端口号");
        tfPort = new JTextField(4);
        tfPort.setText("8765");
        btnConnect = new JButton("连接");
        jp1.add(labelHost);
        jp1.add(tfHost);
        jp1.add(labelPort);
        jp1.add(tfPort);
        jp1.add(btnConnect);
        jp1.setLayout(new FlowLayout(FlowLayout.CENTER));  //设置流式布局，默认从左到右一行

        jp2 = new JPanel();
        labelName = new JLabel("用户名：");
        tfName = new JTextField(15);
        labelPassword = new JLabel("密码：");
        tfPassword = new JTextField(15);
        jp2.add(labelName);
        jp2.add(tfName);
        jp2.add(labelPassword);
        jp2.add(tfPassword);

        jp3 = new JPanel();
        btnLogin = new JButton("登录");
        btnRegister = new JButton("注册");
        jp3.add(btnLogin);
        jp3.add(btnRegister);
        jp3.setLayout(new FlowLayout(FlowLayout.CENTER));

        add(jp1,BorderLayout.NORTH);
        add(jp2,BorderLayout.CENTER);
        add(jp3,BorderLayout.SOUTH);
        setTitle("肌肉猛男聊天室");
        setSize(500, 300);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        // 为登录按钮增加鼠标点击事件监听
        btnLogin.addActionListener(this);
        // 为注册按钮增加鼠标点击事件监听
        btnRegister.addActionListener(this);
        // 为连接按钮增加鼠标点击事件监听
        btnConnect.addActionListener(this);
        // 当窗口关闭时触发
        addWindowListener(new WindowAdapter() { // 窗口关闭后断开连接
            @Override
            public void windowClosing(WindowEvent e) {
                String name = tfName.getText();
                networkService.disconnect(name);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if(e.getSource() == btnLogin){
            String name = tfName.getText();
            String password = tfPassword.getText();
            String host = tfHost.getText();
            int port = Integer.valueOf(tfPort.getText());
            try {
                networkService.login(name,password,host,port);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        else if(e.getSource() == btnRegister){
            String host = tfHost.getText();
            int port = Integer.valueOf(tfPort.getText());
            RegisterView view = new RegisterView(host,port);     //跳转到注册页面，关闭现有页面
        }
        else if(e.getSource() == btnConnect){
            // 响应连接/断开按钮
            if (!networkService.isConnected()) {
                // 未连接状态下，执行连接服务器操作
                String host = tfHost.getText();
                int port = Integer.valueOf(tfPort.getText());
                networkService.connect(host, port);
            } else {
                // 已连接状态下，执行断开连接操作
                String name = tfName.getText();
                networkService.disconnect(name);
            }
        }
    }


    /**
     * 处理NetworkService模块
     * 连接服务器，登录，注册
     */
    //创建NetworkService对象并实现callback接口
    private NetworkService networkService;      //调用NetworkService类
    //创建NetworkService对象并实现callback接口
    private void initNetworkService() {
        networkService = new NetworkService();
        networkService.setCallback(new NetworkService.Callback() {
            @Override
            public void onConnected(String host, int port) {
                // 连接成功时，弹对话框提示，并将按钮文字改为“断开”
                alert("连接", "成功连接到[" + host + ":" + port + "]");
                btnConnect.setText("断开");
            }

            @Override
            public void onConnectFailed(String host, int port) {
                // 连接失败时，弹对话框提示，并将按钮文字设为“连接”
                alert("连接", "无法连接到[" + host + ":" + port + "]");
                btnConnect.setText("连接");
            }

            @Override
            public void onDisconnected() {
                // 断开连接时，弹对话框提示，并将按钮文字设为“连接”
                alert("连接", "连接已断开");
                btnConnect.setText("连接");
            }

            @Override
            public void onLoginChecked(String msg) {
                // 登录成功则跳到功能页面；失败则弹出失败消息
                if(msg.equals("false")) {
                    alert("登录", "登录失败，清检查用户名或密码");
                }
                else{
                    String name = tfName.getText();
                    String host = tfHost.getText();
                    int port = Integer.valueOf(tfPort.getText());
                    //查看离线消息
                    networkService.readOfflineMessage(name,host,port);
                    newFrame();
                }
            }

            @Override
            public void onRegistration(String isValid){
                //todo
            }

            @Override
            public void onNotExist(){
                //todo
            }

            @Override
            public void onRequestChecked(String msg){
                //todo
            }

            @Override
            public void onChecked(String isChecked){
                //todo
            }

            @Override
            public void onGroup(String msg){
                //todo
            }

            @Override
            public void onMessageSent(String name,String msg){
                //todo
            }

            @Override
            public void onGroupReceived(String from_name, String msg,String group){ //group是末尾没有逗号的
                //todo
            }

            @Override
            public void onMessageReceived(String msg){
                //todo
            }
        });
    }

    // 显示标题为title，内容为message的对话框
    private void alert(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    private void newFrame(){
        String host = tfHost.getText();
        int port = Integer.valueOf(tfPort.getText());
        String name = tfName.getText();
        FunctionView view = new FunctionView(host,port,name);
        this.dispose();
    }


    //构造方法
    public PortalView() {
        initView();
        initNetworkService();
    }
    public static void main(String[] args) {
        PortalView view = new PortalView();
    }
}
