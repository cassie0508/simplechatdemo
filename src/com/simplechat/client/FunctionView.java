package com.simplechat.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FunctionView extends JFrame implements
        ActionListener {
    private String host;
    private int port;
    private String name;

    private JButton btnChat;        // 聊天按钮
    private JButton btnAddFriends;        // 加好友按钮
    private JButton btnGroup;        // 群聊按钮
    private JPanel jp1, jp2,jp3;


    private void initView(){
        jp1 = new JPanel();
        btnChat = new JButton("选择好友开启畅聊⚨");
        jp1.add(btnChat);

        jp2 = new JPanel();
        btnAddFriends = new JButton("添加好(meng)友(nan)");
        jp2.add(btnAddFriends);

        jp3 = new JPanel();
        btnGroup = new JButton("发起群聊");
        jp3.add(btnGroup);

        Box vBox = Box.createVerticalBox();
        vBox.add(jp1);
        vBox.add(jp2);
        vBox.add(jp3);
        setContentPane(vBox);
        setTitle("猛男聊天室⚨请选择功能");
        setSize(500, 300);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 为聊天按钮增加鼠标点击事件监听
        btnChat.addActionListener(this);
        // 为加好友按钮增加鼠标点击事件监听
        btnAddFriends.addActionListener(this);
        // 为群聊按钮增加鼠标点击事件监听
        btnGroup.addActionListener(this);
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
        if(e.getSource() == btnAddFriends){
            AddFriendsView view = new AddFriendsView(host,port,name);
            this.dispose();
        }
        if(e.getSource() == btnGroup) {
            AddGroupView view = new AddGroupView(host, port, name);
        }
        if(e.getSource() == btnChat){
            FriendsView view = new FriendsView(host,port,name);
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
                if(msg.charAt(0) == 'c'){
                    msg = msg.substring(1);
                    String []s = msg.split("#");
                    System.out.println(s[0]);
                    System.out.println(name);
                    if(s[0].equals(name)) {
                        System.out.println(s[0]);
                        newCheckFrame(msg);
                    }
                }
            }

            @Override
            public void onChecked(String isChecked){
                //todo
            }

            @Override
            public void onGroup(String msg){
                String []s = msg.split(",");
                for(int i=0;i<s.length;i++){
                    if(s[i].equals(name)){
                        GroupView view = new GroupView(host,port,name,msg);
                    }
                }
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
                System.out.println("3    "+msg);
                if(msg.charAt(0) == 'h'){
                    //收到P2P聊天消息，弹出聊天框
                    System.out.println("4   "+msg);
                    msg = msg.substring(1);
                    String []s = msg.split("#");
                    ChatView view = new ChatView(host,port,s[1],s[0]);  //注意from和to颠倒
                }
            }
        });
    }
    private void alert(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    private void newCheckFrame(String msg){
        CheckView view = new CheckView(host,port,msg);
    }

    public FunctionView(String host, int port,String name) {
        this.name=name;
        this.host=host;
        this.port=port;
        initView();
        initNetworkService();
    }
}
