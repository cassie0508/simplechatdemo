package com.simplechat.server;

import com.simplechat.client.ChatView;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager {

    public ArrayList<ChatSocket> chatSockets = new ArrayList<>();

    public void addClientSocket(Socket socket) throws IOException {
        final ChatSocket chatSocket = new ChatSocket(socket, new ChatSocket.Callback() {

            @Override
            public void onReadSocket(ChatSocket cs, String msg) {
                // TODO Auto-generated method stub
                //好友申请
                if(msg.charAt(0) == 'c'){
                    sendAll(cs, msg);
                }
                //返回好友验证消息 同意/不同意
                if((msg.charAt(0) == 'd') || (msg.charAt(0) == 'e') ){
                    sendAllCheck(cs,msg);
                }
                //拉人进群
                if(msg.charAt(0) == 'f'){
                    sendAllCheck(cs,msg);
                }
                //群聊 给其他客户端发消息，因为自己的View已经由接口onMessageSent实现了
                //msg是g name msg group
                if(msg.charAt(0) == 'g'){
                    sendAll(cs,msg);
                }
            }

            @Override
            public void onError(ChatSocket cs, String error) {
                synchronized (chatSockets) {
                    chatSockets.remove(cs);
                }
            }

        }); //新客户端连接

        synchronized (chatSockets) {
            chatSockets.add(chatSocket); //往客户端管理器里添加客户
        }

        chatSocket.start();
    }

    //向其他客户端发送数据
    public void sendAll(ChatSocket chatSocket, String msg) {
        synchronized (chatSockets) {
            for (ChatSocket cs : chatSockets) {
                if (!cs.equals(chatSocket)) {
                    cs.send(msg);
                }
            }
        }
    }

    //向所有客户端（包括自己）发送好友验证消息
    public void sendAllCheck(ChatSocket chatSocket, String msg) {
        synchronized (chatSockets) {
            for (ChatSocket cs : chatSockets) {
                cs.send(msg);
            }
        }
    }

    public void close() throws IOException {
        synchronized (chatSockets) { //关闭各个连接
            for (ChatSocket socket : chatSockets) {
                socket.close();
            }
            chatSockets.clear();
        }
    }
}