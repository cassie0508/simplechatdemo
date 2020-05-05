package com.simplechat.client;

import com.simplechat.database.DBconn;
import com.simplechat.server.ClientManager;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NetworkService {
    //定义回调接口
    public interface Callback {
        void onConnected(String host, int port);        //连接成功
        void onConnectFailed(String host, int port);    //连接失败
        void onDisconnected();                          //已经断开连接
        void onLoginChecked(String isExist);              //收到登录验证结果
        void onRegistration(String isValid);            //收到注册审批结果
        void onNotExist();                                 //添加好友不存在
        void onRequestChecked(String msg);                     //收到加好友消息  收到发起群聊消息
        void onChecked(String msg);                   //收到好友验证消息 （对方同意/不同意）
        void onGroup(String msg);                       //收到群聊发起消息
        void onMessageSent(String name, String msg);    //消息已经发出
        void onGroupReceived(String name, String msg,String group);   //收到群聊消息
        void onMessageReceived(String msg);//收到P2P消息
    }

    private Callback callback;
    public void setCallback(Callback callback) {
        this.callback = callback;
    }



    // 套接字对象
    private Socket socket = null;
    // 套接字输入流对象，从这里读取收到的消息
    private DataInputStream inputStream = null;
    // 套接字输出流对象，从这里发送聊天消息
    private DataOutputStream outputStream = null;
    // 当前连接状态的标记变量
    private boolean isConnected = false;
    //UDP对象
    private DatagramSocket UDPsocket =  null;

    static public ChatView chatView = null;


    /**
     * 连接到服务器
     * @param host 服务器地址
     * @param port 服务器端口
     */
    public void connect(String host, int port) {
        try {
            // 创建套接字对象，与服务器建立连接
            socket = new Socket(host, port);
            //创建UDP套接字对象，无需连接，仅获得host和port即可 用于实现P2P
            UDPsocket = new DatagramSocket();   //不用连接，随机分配端口号
            isConnected = true;
            // 通知外界已连接
            if (callback != null) {
                callback.onConnected(host, port);
            }
            // 开始侦听是否有聊天消息到来
            //System.out.println("111"+this.chatView);
            beginListening();
            //System.out.println("222"+this.chatView);
            beginListeningUDP();
            //System.out.println("333"+this.chatView);
        } catch (IOException e) {
            // 连接服务器失败
            isConnected = false;
            // 通知外界连接失败
            if (callback != null) {
                callback.onConnectFailed(host, port);
            }
            e.printStackTrace();
        }
    }

    private void beginListening() {
        Runnable listening = new Runnable() {
            @Override
            public void run() {
                try {
                    inputStream = new DataInputStream(socket.getInputStream());
                    while (true) {
                        //System.out.println("TCP  listen:"+chatView);
                        String msg = inputStream.readUTF();
                        if(msg.charAt(0) == 'a'){
                            msg = msg.substring(1);
                            String []s = msg.split("#");
                            String isExist = s[0];
                            if(isExist.equals("true")){
                                String UDP_host = UDPsocket.getLocalAddress().toString();
                                int UDP_port = UDPsocket.getLocalPort();
                                DBconn.init();
                                DBconn.addUpdDel("insert into process(name,host,port) " +
                                        "values('" + s[1] + "','" + UDP_host + "','" + UDP_port + " ')");
                                DBconn.closeConn();
                            }
                            if (callback != null) {
                                callback.onLoginChecked(isExist);
                            }
                        }
                        if(msg.charAt(0) == 'b'){
                            String isValid = msg.substring(1);
                            if(callback != null){
                                callback.onRegistration(isValid);
                            }
                        }
                        if(msg.charAt(0) == 'c'){
                            callback.onRequestChecked(msg); //交给客户去判断自己是不是接收者
                        }
                        if(msg.charAt(0) == 'd'){
                            msg = msg.substring(1);
                            System.out.println(msg);
                            String []s = msg.split("#");

                            callback.onChecked("yes"+"#"+s[2]);
                        }
                        if(msg.charAt(0) == 'e'){
                            msg = msg.substring(1);
                            String []s = msg.split("#");
                            callback.onChecked("no"+"#"+s[2]);
                        }
                        if(msg.charAt(0) == 'f'){
                            msg = msg.substring(1,msg.length()-1);  //去'f'和最后的'，'
                            callback.onGroup(msg);
                        }
                        if(msg.charAt(0) == 'g'){
                            msg = msg.substring(1);
                            String []s = msg.split("#");    //name msg group
                            callback.onGroupReceived(s[0],s[1],s[2]);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        (new Thread(listening)).start();
    }

    private void beginListeningUDP() {
        Runnable listeningUDP = new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buffer=new byte[256];
                    DatagramPacket packet=null;
                    while (true) {
                        //System.out.println("UDP  listen:"+chatView);
                        for(int i=0;i<buffer.length;i++)
                            buffer[i]=(byte)0;
                        packet=new DatagramPacket(buffer, buffer.length);//构建数据报
                        UDPsocket.receive(packet);
                        int len =packet.getLength();
                        String msg = new String(buffer,0,len);
                        if(msg.charAt(0) == 'h') {
                            msg = msg.substring(1);
                            String []s = msg.split("#");
                            chatView= new ChatView(s[2],Integer.parseInt(s[3]),s[1],s[0]);
                        }
                        if(msg.charAt(0) == 'i'){
                            msg = msg.substring(1);
                            String []s = msg.split("#");
                            //设置字体大小
                            SimpleAttributeSet attrset = new SimpleAttributeSet();
                            StyleConstants.setFontSize(attrset,24);
                            //插入内容
                            Document docs = chatView.taChatList.getDocument();//获得文本对象
                            try {
                                docs.insertString(docs.getLength(),    s[1]+ ":\r\n" + s[0] + "\r\n", attrset);//对文本进权行追加
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                        }
                        if(msg.charAt(0)=='j'){
                            msg = msg.substring(1);
                            String []s = msg.split("#");
                            chatView.taChatList.insertIcon(new ImageIcon(s[0]));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        (new Thread(listeningUDP)).start();
    }



    /**
     * 断开连接
     */
    public void disconnect(String name) {
        try {
            if (socket != null) {
                socket.close();
            }
            if(UDPsocket != null){
                UDPsocket.close();
            }
            //删除process里这个进程的这条记录
            DBconn.init();
            System.out.println("wozaizheli");
            DBconn.addUpdDel("delete from process where name='"+name+"'");
            DBconn.closeConn();

            if (inputStream!= null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            isConnected = false;
            // 通知外界连接断开
            if (callback != null) {
                callback.onDisconnected();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 是否已经连接到服务器
     * @return true为已连接，false为未连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 发送登录请求
     */
    public void login(String name,String password,String host,int port) throws IOException {
        if (name == null || "".equals(name) || password == null || "".equals(password)) {
            return;
        }
        if (socket == null) {   //套接字对象必须已创建
            return;
        }


        try {
            // 将消息写入套接字的输出流
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("a" +name + "#" + password);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送注册请求
     */
    public void register(String name,String password,String phone,String host,int port) throws IOException {
        if (socket == null) {   //套接字对象必须已创建
            return;
        }
        if (name == null || "".equals(name) || password == null || "".equals(password) || phone == null || "".equals(phone)) {
            return;
        }
        //System.out.println(UDPsocket);
        /*
        String UDP_host = UDPsocket.getInetAddress().toString();
        int UDP_port = UDPsocket.getPort();
        DBconn.init();
        DBconn.addUpdDel("insert into process(name,host,port) " +
                "values('" + name + "','" + UDP_host + "','" + UDP_port + " ')");
        DBconn.closeConn();
        */

        try {
            // 将消息写入套接字的输出流
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("b" + name + "#" + password + "#" + phone);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录和注册之后：查看离线聊天记录
     */
    public void readOfflineMessage(String name,String host,int port){
        ArrayList<String> list = new ArrayList<>();
        try {
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from message where to_name='" + name + "'");
            while (rs.next()) {
                if (rs.getString("to_name").equals(name) ) {
                    //为了简化实现，离线消息不处理图片
                    list.add(rs.getString("from_name")+" send the message:  "+ rs.getString("content"));
                }
            }
            DBconn.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //如果有记录，就弹出一个窗口显示所有离线消息
        if(list.size()>0){
            OfflineView view = new OfflineView(host,port,list);
        }
        //已经收到离线消息，则该删去该to_name的message
        DBconn.init();
        DBconn.addUpdDel("delete from message where to_name='" + name + "'");
        DBconn.closeConn();
    }

    /**
     * 添加好友请求
     */
    public void addFriends(String name,String phone,String from_name){
        if (name == null || "".equals(name) || phone == null || "".equals(phone)) {
            return;
        }
        if (socket == null) {   //套接字对象必须已创建
            return;
        }
        //判断该用户是否存在，感觉在这里开数据库有安全问题，不过我也不明白
        String flag = "false";
       try {
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from user where name='" + name + "' and phone='" + phone + "'");
            while (rs.next()) {
                if (rs.getString("name").equals(name) && rs.getString("phone").equals(phone)) {
                    flag = "true";
                }
            }
            DBconn.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(flag.equals("false")){
           callback.onNotExist();
           return;
        }

        try {
            // 将消息写入套接字的输出流
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("c" + name + "#" + phone +"#"+ from_name);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同意好友申请
     */
    public void yesRequest(String msg){     //msg里是toName toPhone fromName
        try {
            // 将消息写入套接字的输出流
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("d" + msg);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不同意好友申请
     */
    public void noRequest(String msg){
        try {
            // 将消息写入套接字的输出流
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("e" + msg);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拉群
     */
    public void addGroup(String msg){
        try {
            // 将消息写入套接字的输出流
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("f" + msg);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送群聊消息
     * @param name 用户名
     * @param msg 消息内容
     * @param group 群聊成员
     */

    public void sendMessage(String name, String msg,String group) {
        // 检查参数合法性
        if (name == null || "".equals(name) || msg == null || "".equals(msg)) {
            return;
        }
        if (socket == null) {   //套接字对象必须已创建
            return;
        }
        try {
            // 将消息写入套接字的输出流
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("g" + name + "#" + msg + "#" + group);
            outputStream.flush();
            // 通知外界消息已发送
            if (callback != null) {
                callback.onMessageSent(name, msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * P2P 发送聊天请求 A发起聊天，B弹出窗口
     */
    //判断用户是否在线
    private String onlineCheck(String name){
        String isOnline = "false";
        try {
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from process where name='" + name + "'");
            while (rs.next()) {
                if (rs.getString("name").equals(name)) {
                    isOnline = "true";
                }
            }
            DBconn.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isOnline;
    }


    public void P2Prequest(String host,int port,String from_name,String to_name,ChatView chatView) throws IOException {
        this.chatView = chatView;
       // System.out.println("fangfanei:"+this.chatView);
        //beginListeningUDP();
        String isOnline = onlineCheck(to_name);
        //如果对方在线
        if(isOnline.equals("true")){

            String msg = "h"+from_name+"#"+to_name+"#"+host+"#"+port;
            int to_port = 0;
            //得到接收方的ip地址和端口号
            try {
                DBconn.init();
                ResultSet rs = DBconn.selectSql("select * from process where name='" + to_name + "'");
                while (rs.next()) {
                    if (rs.getString("name").equals(to_name)) {
                        to_port = rs.getInt("port");
                    }
                }
                DBconn.closeConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //创建发送数据报
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName("127.0.0.1") ,to_port);
            UDPsocket.send(packet);
        }
        //如果对方不在线，没有任何反应
    }


    public void sendP2P(String msg,String from_name,String to_name) throws IOException {
        String isOnline = onlineCheck(to_name);
        if(isOnline.equals("true")){
            String msg2 = msg+"#"+from_name+"#"+to_name;
            int to_port = 0;
            try {
                DBconn.init();
                ResultSet rs = DBconn.selectSql("select * from process where name='" + to_name + "'");
                while (rs.next()) {
                    if (rs.getString("name").equals(to_name)) {
                        to_port = rs.getInt("port");
                    }
                }
                DBconn.closeConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DatagramPacket packet = new DatagramPacket(msg2.getBytes(), msg2.getBytes().length, InetAddress.getByName("127.0.0.1") ,to_port);
            UDPsocket.send(packet);
            msg = msg.substring(1);
        }
        //对方不在线，离线聊天记录
        else if(isOnline.equals("false")){
            //不管普通消息还是图片，都先放进去，等用户上线再处理
            msg = msg.substring(1);
            DBconn.init();
            DBconn.addUpdDel("insert into message(from_name,to_name,content) " +
                    "values('" + from_name + "','" + to_name + "','" + msg  + " ')");
            DBconn.closeConn();
        }

        callback.onMessageSent(from_name,msg);
    }

}
