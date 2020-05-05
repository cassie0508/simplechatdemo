package com.simplechat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddFriendsView extends JFrame implements
        ActionListener {
    private String host;
    private int port;
    private String myName;

    private JTextField tfName;      // 用户名输入框
    private JTextField tfPhone;   // 电话输入框
    private JButton btnAdd;        // 添加按钮
    private JButton btnBack;        // 返回按钮

    private JLabel labelName;
    private JLabel labelPhone;
    private JPanel jp1, jp2, jp3,jp4;

    private void initView() {
        jp1 = new JPanel();
        labelName = new JLabel("用户名：");
        tfName = new JTextField(15);
        jp1.add(labelName);
        jp1.add(tfName);

        jp2 = new JPanel();
        labelPhone = new JLabel("电话：");
        tfPhone = new JTextField(15);
        jp2.add(labelPhone);
        jp2.add(tfPhone);

        jp3 = new JPanel();
        btnAdd = new JButton("添加好友");
        jp3.add(btnAdd);
        jp3.setLayout(new FlowLayout(FlowLayout.CENTER));

        jp4 = new JPanel();
        btnBack = new JButton("返回到功能页面");
        jp4.add(btnBack);
        jp4.setLayout(new FlowLayout(FlowLayout.CENTER));

        Box vBox = Box.createVerticalBox();
        vBox.add(jp1);
        vBox.add(jp2);
        vBox.add(jp3);
        vBox.add(jp4);
        setContentPane(vBox);
        setTitle("清输入你想要添加的人的用户名和电话");
        setSize(500, 300);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 为添加好友按钮增加鼠标点击事件监听
        btnAdd.addActionListener(this);
        // 为返回按钮增加鼠标点击事件监听
        btnBack.addActionListener(this);
        // 当窗口关闭时触发
        addWindowListener(new WindowAdapter() { // 窗口关闭后断开连接
            @Override
            public void windowClosing(WindowEvent e) {
                networkService.disconnect(myName);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnAdd){
            String name = tfName.getText();
            String phone = tfPhone.getText();
            networkService.addFriends(name,phone,myName);
        }
        else if(e.getSource() == btnBack){
            FunctionView view = new FunctionView(host,port,myName);
            this.dispose();
        }
    }

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
                //todo
            }

            @Override
            public void onNotExist(){
                alert("添加好友", "该用户不存在!");
            }

            @Override
            public void onRequestChecked(String msg){
                //todo
            }

            @Override
            public void onChecked(String msg){
                String s[]=msg.split("#");        //from_name 判断自己是否时发出好友申请的人
                if(s[1].equals(myName)) {
                    System.out.println(s[0]);
                    if (s[0].equals("yes")) {
                        alert("添加好友", "添加成功");
                    } else if (s[0].equals("no")) {
                        alert("添加好友", "对方拒绝了你的好友申请");
                    }
                }
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
    private void newCheckFrame(String msg){
        CheckView view = new CheckView(host,port,msg);
    }

    public AddFriendsView(String host, int port,String myName) {
        this.host=host;
        this.port=port;
        this.myName=myName;
        initView();
        initNetworkService();
    }
}
