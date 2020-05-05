package com.simplechat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddGroupView extends JFrame implements
        ActionListener {
    private String host;
    private int port;
    private String name;

    private JLabel labelFriends;     //群聊好友
    private JTextField tfFriends;      // 群聊好友输入框
    private JButton btnAddGroup;        // 确定按钮
    private JPanel jp1,jp2;

    private void initView(){
        jp1 = new JPanel();
        labelFriends = new JLabel("拉好友进群：");    //name用逗号隔开
        tfFriends = new JTextField(15);
        jp1.add(labelFriends);
        jp1.add(tfFriends);
        jp1.setLayout(new FlowLayout(FlowLayout.CENTER));

        jp2 = new JPanel();
        btnAddGroup = new JButton("确定");
        jp2.add(btnAddGroup);

        add(jp1, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);
        setTitle("拉好友进群");
        setSize(500, 500);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 为拉群按钮增加鼠标点击事件监听
        btnAddGroup.addActionListener(this);
        // 当窗口关闭时触发
        addWindowListener(new WindowAdapter() { // 窗口关闭后断开连接
            @Override
            public void windowClosing(WindowEvent e) {
                networkService.disconnect(name);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnAddGroup){
            String msg = name+","+tfFriends.getText();
            networkService.addGroup(msg);
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

    public AddGroupView(String host, int port,String name) {
        this.name=name;
        this.host=host;
        this.port=port;
        initView();
        initNetworkService();
    }
}
