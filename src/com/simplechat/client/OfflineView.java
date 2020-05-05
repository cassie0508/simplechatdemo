package com.simplechat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class OfflineView extends JFrame implements
        ActionListener {
    private String host;
    private int port;
    private ArrayList<String> list;

    private JTextArea taChatList;   // 聊天内容区
    private JScrollPane scrollPane;

    private JButton btnSend;        // 已读按钮
    private JPanel jp2;

    private void initView(){
        taChatList = new JTextArea(20, 20);
        taChatList.setEditable(false);
        scrollPane = new JScrollPane(taChatList);
        for(int i=0;i<list.size();i++){
            taChatList.append(list.get(i)+ "\r\n");
        }

        jp2 = new JPanel();
        btnSend = new JButton("已读");
        jp2.add(btnSend);

        add(scrollPane, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);
        setTitle("离线消息");
        setSize(500, 500);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 为已读按钮增加鼠标点击事件监听
        btnSend.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSend){
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
            public void onGroupReceived(String from_name, String msg,String group){
                //todo
            }

            @Override
            public void onMessageReceived(String msg){
                //todo
            }
        });
    }

    public OfflineView(String host, int port, ArrayList<String>list) {
        this.host=host;
        this.port=port;
        this.list=list;
        initView();
        initNetworkService();
    }
}

