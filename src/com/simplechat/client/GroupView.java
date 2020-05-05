package com.simplechat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GroupView extends JFrame implements
        ActionListener {
    private String host;
    private int port;
    private String name;
    private String group;

    private JTextArea taChatList;   // 聊天内容区

    private JLabel labelName;   //自己的name
    private JTextField tfMessage;   // 聊天输入框
    private JButton btnSend;        // 发送按钮
    private JPanel jp2;

    private JScrollPane scrollPane;

    private void initView(){
        taChatList = new JTextArea(20, 20);
        taChatList.setEditable(false);
        scrollPane = new JScrollPane(taChatList);

        jp2 = new JPanel();
        labelName =new JLabel(name);
        tfMessage = new JTextField(15);
        btnSend = new JButton("发送");
        jp2.add(labelName);
        jp2.add(tfMessage);
        jp2.add(btnSend);
        jp2.setLayout(new FlowLayout(FlowLayout.CENTER));

        add(scrollPane, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);
        setTitle("群聊");
        setSize(500, 500);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 为发送按钮增加鼠标点击事件监听
        btnSend.addActionListener(this);
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
        if(e.getSource() == btnSend){
            String msg = tfMessage.getText();
            networkService.sendMessage(name,msg,group);
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
                tfMessage.setText("");
                taChatList.append("俺(" + name + "):\r\n" + msg + "\r\n");
            }

            @Override
            public void onGroupReceived(String from_name, String msg,String group){ //group是末尾没有逗号的
                String []s = group.split(",");
                for(int i=0;i<s.length;i++){
                    if(s[i].equals(name)){
                        taChatList.append(from_name + ":\r\n" + msg + "\r\n");
                        break;
                    }
                }
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

    public GroupView(String host, int port,String name,String group) {
        this.name=name;
        this.host=host;
        this.port=port;
        this.group=group;
        initView();
        initNetworkService();
    }
}
