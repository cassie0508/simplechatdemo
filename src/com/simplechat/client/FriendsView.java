package com.simplechat.client;

import com.simplechat.database.DBconn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendsView extends JFrame implements
        ActionListener {

    private String host;
    private int port;
    private String name;
    String []friends;

    private JTextArea taFriendsList;   // 好友列表
    private JScrollPane scrollPane;

    private JLabel labelChosen;   //选择聊天对象
    private JTextField tfChosen;
    private JButton btnChoose;        // 选择按钮

    private JPanel jp1;

    private void initView(){
        taFriendsList = new JTextArea(20, 20);
        taFriendsList.setEditable(false);
        scrollPane = new JScrollPane(taFriendsList);
        String s="";
        //从数据库中获得好友列表
        try {
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from user where name='" + name +"'");
            while (rs.next()) {
                if (rs.getString("name").equals(name) ) {
                    s = rs.getString("friends");
                    break;
                }
            }
            DBconn.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        s = s.substring(0,s.length()-1);  //去掉末尾的“,”
        friends = s.split(",");
        taFriendsList.append("好友列表：" + "\r\n");
        for(int i=0;i<friends.length;i++){
            taFriendsList.append(friends[i] + "\r\n");
        }

        jp1 = new JPanel();
        labelChosen = new JLabel("输入好友name:");
        tfChosen = new JTextField(12);
        btnChoose = new JButton("确定");
        jp1.add(labelChosen);
        jp1.add(tfChosen);
        jp1.add(btnChoose);
        jp1.setLayout(new FlowLayout(FlowLayout.CENTER));

        add(scrollPane, BorderLayout.CENTER);
        add(jp1, BorderLayout.SOUTH);
        setTitle("选择好友聊天");
        setSize(500, 500);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 为确定按钮增加鼠标点击事件监听
        btnChoose.addActionListener(this);
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
        String to_name = tfChosen.getText();
        int flag=0; //非好友
        for(int i=0;i<friends.length;i++){
            if(friends[i].equals(to_name)){
                flag = 1;
                break;
            }
        }
        if(flag == 0){
            alert("聊天", "你没有这个好友！");
        }
        else{
            ChatView view = new ChatView(host,port,name,to_name);   //自己弹出聊天框
            try {
                networkService.P2Prequest(host,port,name,to_name,view);    //对方弹出聊天框
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            this.dispose();
        }
    }

    private NetworkService networkService;
    private void initNetworkService() {
        networkService = new NetworkService();
        networkService.connect(host, port);
        networkService.setCallback(new NetworkService.Callback() {
            @Override
            public void onConnected(String host, int port) {
                //todo
            }

            @Override
            public void onConnectFailed(String host, int port) {
                //todo
            }

            @Override
            public void onDisconnected() {
                // 断开连接时，弹对话框提示，并将按钮文字设为“连接”
                alert("连接", "连接已断开");
            }

            @Override
            public void onLoginChecked(String isExist) {
                //todo
            }

            @Override
            public void onRegistration(String isValid) {
                //todo
            }

            @Override
            public void onNotExist() {
                //todo
            }

            @Override
            public void onRequestChecked(String msg) {
                //todo
            }

            @Override
            public void onChecked(String isChecked) {
                //todo
            }

            @Override
            public void onGroup(String msg) {
                //todo
            }

            @Override
            public void onMessageSent(String name, String msg) {
                //todo
            }

            @Override
            public void onGroupReceived(String from_name, String msg, String group) {
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

    public FriendsView(String host, int port,String name) {
        this.name=name;
        this.host=host;
        this.port=port;
        initView();
        initNetworkService();
    }
}

