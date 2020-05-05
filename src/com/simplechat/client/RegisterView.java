package com.simplechat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import com.simplechat.client.PortalView;

public class RegisterView extends JFrame implements
        ActionListener {
    private String host;
    private int port;

    private JTextField tfName;      // 用户名输入框
    private JTextField tfPassword;   // 密码输入框
    private JTextField tfPhone;   // 电话输入框
    private JButton btnRegister;        // 注册按钮

    private JLabel labelName;
    private JLabel labelPassword;
    private JLabel labelPhone;
    private JPanel jp1, jp2, jp3,jp4;


    /**
     * 处理图形化界面
     * 注册页面，注册成功后转到功能页面
     */
    //用户界面初始化
    private void initView() {
        jp1 = new JPanel();
        labelName = new JLabel("用户名：");
        tfName = new JTextField(15);
        jp1.add(labelName);
        jp1.add(tfName);

        jp2 = new JPanel();
        labelPassword = new JLabel("密码：");
        tfPassword = new JTextField(15);
        jp2.add(labelPassword);
        jp2.add(tfPassword);

        jp3 = new JPanel();
        labelPhone = new JLabel("电话：");
        tfPhone = new JTextField(15);
        jp3.add(labelPhone);
        jp3.add(tfPhone);

        jp4 = new JPanel();
        btnRegister = new JButton("注册");
        jp4.add(btnRegister);
        jp4.setLayout(new FlowLayout(FlowLayout.CENTER));

        Box vBox = Box.createVerticalBox();
        vBox.add(jp1);
        vBox.add(jp2);
        vBox.add(jp3);
        vBox.add(jp4);
        setContentPane(vBox);
        setTitle("肌肉猛男聊天室");
        setSize(500, 300);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        // 为注册按钮增加鼠标点击事件监听
        btnRegister.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if(e.getSource() == btnRegister){
            String name = tfName.getText();
            String password = tfPassword.getText();
            String phone = tfPhone.getText();
            try {
                networkService.register(name,password,phone,host,port);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 处理NetworkService模块
     * 注册
     */
    private NetworkService networkService;
    private void initNetworkService(){
        networkService = new NetworkService();
        networkService.connect(host, port);
        networkService.setCallback(new NetworkService.Callback() {
            @Override
            public void onConnected(String host, int port){
                //todo
            }

            @Override
            public void onConnectFailed(String host, int port){
                //todo
            }

            @Override
            public void onDisconnected() {
                // 断开连接时，弹对话框提示，并将按钮文字设为“连接”
                alert("连接", "连接已断开");
            }

            @Override
            public void onLoginChecked(String isExist){
                //todo
            }

            @Override
            public void onRegistration(String isValid){
                // 注册成功则跳到功能页面；失败则弹出失败消息
                if(isValid.equals("false")) {
                    alert("注册", "注册失败，清重新注册");
                }
                else{
                    //查看离线消息
                    String name = tfName.getText();
                    networkService.readOfflineMessage(name,host,port);
                    //newFrame();
                    alert("注册", "注册成功");
                    delFrame();
                }
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
    private void alert(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    /*
    private void newFrame(){
        String name = tfName.getText();
        FunctionView view = new FunctionView(host,port,name);
        this.dispose();
    }
    */
    private void delFrame(){
        this.dispose();
    }

    public RegisterView(String host, int port) {
        this.host=host;
        this.port=port;
        initView();
        initNetworkService();
    }
}

