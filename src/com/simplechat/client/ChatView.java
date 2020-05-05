package com.simplechat.client;

import com.simplechat.database.DBconn;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatView extends JFrame implements
        ActionListener {
    private String host;
    private int port;
    private String from_name;
    private String to_name;

    public JTextPane taChatList;   // 聊天内容区
    private JScrollPane scrollPane;

    private JLabel labelName;   //自己的name
    private JTextField tfMessage;   // 聊天输入框
    private JButton btnSend;        // 发送按钮
    private JButton btnSendPic;        // 发送图片按钮
    public JPanel jp2;


    private void initView(){
        taChatList = new JTextPane();
        taChatList.setEditable(false);
        scrollPane = new JScrollPane(taChatList);

        jp2 = new JPanel();
        labelName =new JLabel(from_name);
        tfMessage = new JTextField(15);
        btnSend = new JButton("发送");
        btnSendPic = new JButton("发送图片");
        jp2.add(labelName);
        jp2.add(tfMessage);
        jp2.add(btnSend);
        jp2.add(btnSendPic);
        jp2.setLayout(new FlowLayout(FlowLayout.CENTER));

        add(scrollPane, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);
        setTitle("聊天");
        setSize(500, 500);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 为发送按钮增加鼠标点击事件监听
        btnSend.addActionListener(this);
        // 为发送图片按钮增加鼠标点击事件监听
        btnSendPic.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e){
        String msg = tfMessage.getText();
        if(e.getSource() == btnSend){
            msg = "i"+msg;
        }
        if(e.getSource() == btnSendPic){
            msg = "j"+msg;
        }
        try {
            networkService.sendP2P(msg,from_name,to_name);
        } catch (IOException e1) {
            e1.printStackTrace();
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
                //设置字体大小
                SimpleAttributeSet attrset = new SimpleAttributeSet();
                StyleConstants.setFontSize(attrset,24);
                //插入内容
                Document docs = taChatList.getDocument();//获得文本对象
                try {
                    docs.insertString(docs.getLength(), "俺(" + name + "):\r\n" + msg + "\r\n", attrset);//对文本进权行追加
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGroupReceived(String from_name, String msg,String group){
                //todo
            }

            @Override
            public void onMessageReceived(String msg){
                //todo h的话什么都不干
            }
        });
    }
    private void alert(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public ChatView(String host, int port,String from_name,String to_name) {
        this.from_name=from_name;
        this.to_name=to_name;
        this.host=host;
        this.port=port;
        initView();
        initNetworkService();
    }
}
