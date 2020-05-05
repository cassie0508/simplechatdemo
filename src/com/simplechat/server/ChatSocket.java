package com.simplechat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.simplechat.database.DBconn;
import com.simplechat.database.user;

public class ChatSocket{
    public interface Callback {
        void onReadSocket(ChatSocket chatSocket, String msg);
        void onError(ChatSocket chatSocket, String error);
    }

    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private Callback callback = null;

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public ChatSocket(Socket socket,Callback callback) throws IOException {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.callback = callback;
    }

    /**
     * 服务器实现的方法
     */
    public void send(String send) { // 向客户端发送数据
        try {
            outputStream.writeUTF(send);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginCheck(String msg) {
        String isExist = "false";     //判断该用户是否存在于数据库中
        msg = msg.substring(1);     //去掉用于判断类型的第一个字符
        String[] s = msg.split("#");
        try {
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from user where name='" + s[0] + "' and password='" + s[1] + "'");
            while (rs.next()) {
                if (rs.getString("name").equals(s[0]) && rs.getString("password").equals(s[1])) {
                    isExist = "true";
                }
            }
            DBconn.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //向客户端返回验证消息
        try {
            outputStream.writeUTF("a" + isExist+"#"+s[0]);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registration(String msg){
        String isValid = "true";        //审批注册信息是否有效
        msg = msg.substring(1);     //去掉用于判断类型的第一个字符
        String[] s = msg.split("#");
        //检查格式，这里检查得非常简单，实际情况更复杂
        System.out.println(s);
        if (s[0] == null || "".equals(s[0]) || s[1] == null || "".equals(s[1]) || s[2] == null || "".equals(s[2])) {
            isValid="false";
        }
        //检查是否已存在，若已存在则返回错误信息，理应做出提示“用户已存在”，但这里为实现简单不做提示
        try {
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from user where name='" + s[0] + "'");
            while (rs.next()) {
                if (rs.getString("name").equals(s[0]) ) {
                    isValid="false";
                }
            }
            DBconn.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(isValid.equals("true")) {
            DBconn.init();
            DBconn.addUpdDel("insert into user(name,password,phone,is_online,friends) " +
                    "values('" + s[0] + "','" + s[1] + "','" + s[2] + "'," + 1 + ",'" + "')");
            DBconn.closeConn();
        }

        //向客户端返回验证消息
        try {
            outputStream.writeUTF("b" + isValid);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //同意好友申请  msg内容是toName toPhone fromName
    public void yesAdd(String msg){
        msg = msg.substring(1);
        String[] s = msg.split("#");
        //添加好友的方法是 先得到现有friends  再update新的。 对to和from都要添加
        String toFriends="",fromFriends="";
        try {
            DBconn.init();
            ResultSet rs = DBconn.selectSql("select * from user where name='" + s[0] +"'");
            while (rs.next()) {
                if (rs.getString("name").equals(s[0])) {
                    toFriends = rs.getString("friends");
                }
            }
            System.out.println(toFriends);
            toFriends = toFriends+s[2]+",";
            DBconn.addUpdDel("update user set friends='"+ toFriends +"' where name = '"+s[0]+ "'");

            ResultSet rs2 = DBconn.selectSql("select * from user where name='" + s[2] +"'");
            while (rs2.next()) {
                if (rs2.getString("name").equals(s[2])) {
                    fromFriends = rs2.getString("friends");
                }
            }
            fromFriends = fromFriends+s[0]+",";
            DBconn.addUpdDel("update user set friends='"+ fromFriends +"' where name = '"+s[2]+ "'");

            DBconn.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    /**
     * 服务器开关
     */
    public void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String accept = null;
                while (true) {  //一直等待客户端发来的请求
                    try {
                        accept = inputStream.readUTF();
                        if (callback != null) {
                            if(accept.charAt(0) == 'a'){
                                loginCheck(accept);
                            }
                            if(accept.charAt(0) == 'b'){
                                registration(accept);
                            }
                            if(accept.charAt(0) == 'c'){
                                callback.onReadSocket(ChatSocket.this,accept);
                            }
                            if(accept.charAt(0) == 'd'){
                                yesAdd(accept);     //先再数据库中更新friends
                                callback.onReadSocket(ChatSocket.this,accept); //再发消息回去
                            }
                            if(accept.charAt(0) == 'e'){
                                callback.onReadSocket(ChatSocket.this,accept);
                            }
                            if(accept.charAt(0) == 'f'){
                                callback.onReadSocket(ChatSocket.this,accept);
                            }
                            if(accept.charAt(0) == 'g'){
                                callback.onReadSocket(ChatSocket.this,accept);
                            }
                        }
                    } catch (IOException e) {
                        if (callback != null) {
                            callback.onError(ChatSocket.this, e.getMessage());
                        }
                    }
                }
            }
        });
        thread.start();
    }

    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
    }

}
