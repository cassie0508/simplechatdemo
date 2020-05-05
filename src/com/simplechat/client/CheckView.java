package com.simplechat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CheckView extends JFrame implements
        ActionListener {
    private String host;
    private int port;
    private String msg;

    private JLabel labelFromName;
    private JButton btnYes;        // 同意按钮
    private JButton btnNo;        // 不同意按钮
    private JPanel jp1, jp2;

    private void initView(){
        jp1 = new JPanel();
        String []s = msg.split("#");
        labelFromName = new JLabel("你接受来自"+s[2]+"的好友申请吗？");
        jp1.add(labelFromName);

        jp2 = new JPanel();
        btnYes = new JButton("是");
        btnNo = new JButton("否");
        jp2.add(btnYes);
        jp2.add(btnNo);
        jp2.setLayout(new FlowLayout(FlowLayout.CENTER));

        add(jp1,BorderLayout.CENTER);
        add(jp2,BorderLayout.SOUTH);
        setTitle("好友申请审核");
        setSize(500, 300);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 为同意按钮增加鼠标点击事件监听
        btnYes.addActionListener(this);
        // 为不同意按钮增加鼠标点击事件监听
        btnNo.addActionListener(this);

        //注意和其他不一样的是，窗口关闭后什么都不做
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnYes){
            networkService.yesRequest(msg);
            this.dispose();
        }
        else if(e.getSource() == btnNo){
            networkService.noRequest(msg);
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
                //todo
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
            public void onRequestChecked(String from_name){
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


    public CheckView(String host,int port,String msg){
        this.host=host;
        this.port=port;
        this.msg=msg;
        initView();
        initNetworkService();
    }
}
